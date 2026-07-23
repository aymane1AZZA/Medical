package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.ObservationCreate;
import com.chu.sih.dto.ClinicalRequests.SessionCreate;
import com.chu.sih.dto.ClinicalRequests.SessionMetricsUpdate;
import com.chu.sih.dto.ClinicalRequests.Transition;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ApheresisSessionService {
    private static final List<String[]> DEFAULT_CHECKLIST = List.of(
            new String[]{"IDENTITY","Identite verifiee avec deux identifiants"},
            new String[]{"CONSENT","Consentement valide verifie"},
            new String[]{"PRESCRIPTION","Prescription validee et active"},
            new String[]{"LABS","Bilans pre-seance conformes"},
            new String[]{"ACCESS","Acces vasculaire evalue"},
            new String[]{"DEVICE","Appareil, kit et auto-test verifies"},
            new String[]{"SUPPLIES","Produits et medicaments disponibles"}
    );
    private final ApheresisSessionRepository repository;
    private final ApheresisPrescriptionRepository prescriptions;
    private final EquipmentRepository equipment;
    private final ConsentRepository consents;
    private final SessionChecklistItemRepository checklist;
    private final SessionEventRepository events;
    private final SessionObservationRepository observations;
    private final SessionAlarmRepository alarms;
    private final CurrentActor actor;
    private final AuditService audit;
    private final SessionStateMachine stateMachine;
    private final ClinicalAccessService access;
    private final SessionAlarmService alarmService;
    private final RealTimeEventService realtime;

    @Transactional(readOnly=true) public ApheresisSession get(UUID id){var value=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Seance introuvable."));access.requirePatient(value.getPatientId());return value;}
    @Transactional(readOnly=true) public List<ApheresisSession> forPatient(UUID patientId){access.requirePatient(patientId);return repository.findByPatientIdOrderByCreatedAtDesc(patientId);}
    @Transactional(readOnly=true) public List<SessionChecklistItem> checklist(UUID id){get(id);return checklist.findBySessionIdOrderByItemCode(id);}
    @Transactional(readOnly=true) public List<SessionObservation> observations(UUID id){get(id);return observations.findBySessionIdOrderByObservedAtDesc(id);}

    @Transactional public ApheresisSession create(SessionCreate request){
        access.requirePatient(request.patientId());
        var prescription=prescriptions.findById(request.prescriptionId()).orElseThrow(()->new ResourceNotFoundException("Prescription introuvable."));
        if(!prescription.getPatientId().equals(request.patientId())) throw new BadRequestException("La prescription n'appartient pas a ce patient.");
        if(!List.of("VALIDATED","ACTIVE").contains(prescription.getStatus())) throw new BadRequestException("La prescription doit etre validee.");
        if(repository.countByPrescriptionId(request.prescriptionId())>=prescription.getSessionsPlanned()) throw new BadRequestException("Le nombre de seances prescrit est deja atteint.");
        var session=repository.save(ApheresisSession.builder().sessionNumber(nextNumber()).patientId(request.patientId())
                .prescriptionId(request.prescriptionId()).appointmentId(request.appointmentId()).equipmentId(request.equipmentId())
                .locationId(request.locationId()).sequenceNumber(request.sequenceNumber()).plannedVolumeMl(request.plannedVolumeMl())
                .vascularAccess(request.vascularAccess()).status("PLANNED").build());
        checklist.saveAll(DEFAULT_CHECKLIST.stream().map(item->SessionChecklistItem.builder().sessionId(session.getId())
                .itemCode(item[0]).label(item[1]).mandatory(true).status("PENDING").build()).toList());
        event(session,null,"PLANNED","CREATED",null);
        audit.record("APHERESIS_SESSION_CREATED","CREATE","ApheresisSession",session.getId(),request.patientId(),"{}");
        return session;
    }

    @Transactional public SessionChecklistItem completeChecklist(UUID sessionId, UUID itemId, String comment){
        var session=get(sessionId);
        var item=checklist.findById(itemId).orElseThrow(()->new ResourceNotFoundException("Element de checklist introuvable."));
        if(!item.getSessionId().equals(sessionId)) throw new BadRequestException("Element de checklist incoherent.");
        item.setStatus("COMPLETED");item.setCompletedAt(Instant.now());item.setCompletedBy(actor.id());item.setComment(comment);
        audit.record("SESSION_CHECKLIST_COMPLETED","UPDATE","SessionChecklistItem",itemId,session.getPatientId(),"{}");
        return item;
    }

    @Transactional public ApheresisSession transition(UUID id, Transition request){
        var session=get(id);String target=request.targetStatus().trim().toUpperCase();
        stateMachine.assertAllowed(session.getStatus(),target,request.reason());
        if(target.equals("READY")){
            if(checklist.countBySessionIdAndMandatoryTrueAndStatusNot(id,"COMPLETED")>0) throw new BadRequestException("La checklist obligatoire doit etre terminee.");
            if(!consents.existsByPatientIdAndConsentTypeAndStatus(session.getPatientId(),"APHERESIS","ACTIVE")) throw new BadRequestException("Aucun consentement d'apherese actif.");
            if(session.getEquipmentId()!=null){
                var device=equipment.findById(session.getEquipmentId()).orElseThrow(()->new ResourceNotFoundException("Equipement introuvable."));
                if(!List.of("AVAILABLE","RESERVED").contains(device.getStatus())) throw new BadRequestException("L'equipement n'est pas disponible.");
                if(device.getNextMaintenanceAt()!=null && device.getNextMaintenanceAt().isBefore(Instant.now())) throw new BadRequestException("La maintenance de l'equipement est echue.");
            }
        }
        if(List.of("COMPLETED","ABORTED","VALIDATED").contains(target) && alarms.countBySessionIdAndSeverityAndResolvedAtIsNull(id,"CRITICAL")>0)
            throw new BadRequestException("Une alarme critique non resolue bloque cette transition.");
        if(target.equals("COMPLETED") && (session.getActualProcessedVolumeMl()==null || session.getActualProcessedVolumeMl().signum()<=0))
            throw new BadRequestException("Le volume reel traite doit etre renseigne avant de terminer.");
        String previous=session.getStatus();session.setStatus(target);
        if(target.equals("IN_PROGRESS") && session.getStartedAt()==null){session.setStartedAt(Instant.now());setEquipmentStatus(session,"IN_USE");}
        if(List.of("COMPLETED","ABORTED").contains(target)){session.setEndedAt(Instant.now());session.setTerminationReason(request.reason());setEquipmentStatus(session,"AVAILABLE");}
        if(target.equals("VALIDATED")){
            if(request.clinicalSummary()==null||request.clinicalSummary().isBlank()) throw new BadRequestException("La synthese clinique est obligatoire pour valider.");
            session.setValidatedAt(Instant.now());session.setValidatedBy(actor.id());session.setClinicalSummary(request.clinicalSummary());
        }
        event(session,previous,target,"STATUS_CHANGED",request.reason());
        audit.record("SESSION_STATUS_CHANGED","UPDATE","ApheresisSession",id,session.getPatientId(),"{\"to\":\""+target+"\"}");
        realtime.session(id,"session.status.changed","{\"from\":\""+previous+"\",\"to\":\""+target+"\"}");
        return session;
    }

    @Transactional public SessionObservation addObservation(UUID sessionId, ObservationCreate request){
        var session=get(sessionId);
        if(!List.of("IN_PROGRESS","PAUSED").contains(session.getStatus())) throw new BadRequestException("La surveillance ne peut etre saisie que pendant une seance active.");
        var value=observations.save(SessionObservation.builder().sessionId(sessionId).observationCode(request.observationCode())
                .codeSystem(request.codeSystem()).valueNumeric(request.valueNumeric()).valueText(request.valueText()).unitUcum(request.unitUcum())
                .source(request.source()==null?"MANUAL":request.source()).deviceId(request.deviceId()).observedAt(request.observedAt())
                .recordedBy(actor.id()).validationStatus("FINAL").build());
        audit.record("SESSION_OBSERVATION_RECORDED","CREATE","SessionObservation",value.getId(),session.getPatientId(),"{\"code\":\""+request.observationCode()+"\"}");
        alarmService.evaluate(session,value);
        realtime.session(sessionId,"session.observation.created","{\"observationId\":\""+value.getId()+"\",\"code\":\""+request.observationCode()+"\"}");
        return value;
    }

    @Transactional public ApheresisSession updateMetrics(UUID sessionId, SessionMetricsUpdate request){
        var session=get(sessionId);
        if(!List.of("IN_PROGRESS","PAUSED","COMPLETED").contains(session.getStatus())) throw new BadRequestException("Le bilan volumique n'est pas modifiable dans cet etat.");
        session.setActualProcessedVolumeMl(request.actualProcessedVolumeMl());session.setReplacementVolumeMl(request.replacementVolumeMl());
        session.setAnticoagulantVolumeMl(request.anticoagulantVolumeMl());session.setFluidBalanceMl(request.fluidBalanceMl());
        audit.record("SESSION_METRICS_UPDATED","UPDATE","ApheresisSession",sessionId,session.getPatientId(),"{}");
        realtime.session(sessionId,"session.metrics.updated","{\"sessionId\":\""+sessionId+"\"}");
        return session;
    }

    private void event(ApheresisSession session,String from,String to,String type,String reason){events.save(SessionEvent.builder().sessionId(session.getId()).eventType(type).fromStatus(from).toStatus(to).reason(reason).recordedBy(actor.id()).build());}
    private void setEquipmentStatus(ApheresisSession session,String status){if(session.getEquipmentId()!=null){var device=equipment.findById(session.getEquipmentId()).orElseThrow(()->new ResourceNotFoundException("Equipement introuvable."));device.setStatus(status);}}
    private String nextNumber(){return "APH-"+DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(Instant.now())+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();}
}
