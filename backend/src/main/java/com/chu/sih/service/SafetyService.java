package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.CorrectiveActionComplete;
import com.chu.sih.dto.ClinicalRequests.CorrectiveActionCreate;
import com.chu.sih.dto.ClinicalRequests.IncidentCreate;
import com.chu.sih.dto.ClinicalRequests.IncidentTransition;
import com.chu.sih.entity.CorrectiveAction;
import com.chu.sih.entity.Incident;
import com.chu.sih.entity.Notification;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.CorrectiveActionRepository;
import com.chu.sih.repository.IncidentRepository;
import com.chu.sih.repository.NotificationRepository;
import com.chu.sih.repository.UserRepository;
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
public class SafetyService {
    private final IncidentRepository repository;
    private final CorrectiveActionRepository actions;
    private final NotificationRepository notifications;
    private final UserRepository users;
    private final CurrentActor actor;
    private final AuditService audit;
    private final ClinicalAccessService access;
    private final RealTimeEventService realtime;

    @Transactional(readOnly=true) public List<Incident> open(){return repository.findByStatusNotOrderByOccurredAtDesc("CLOSED").stream().filter(i->i.getPatientId()==null||access.canAccessPatient(i.getPatientId())).toList();}
    @Transactional(readOnly=true) public Incident get(UUID id){var value=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Incident introuvable."));if(value.getPatientId()!=null)access.requirePatient(value.getPatientId());return value;}
    @Transactional(readOnly=true) public List<CorrectiveAction> actions(UUID incidentId){get(incidentId);return actions.findByIncidentIdOrderByDueAt(incidentId);}

    @Transactional public Incident create(IncidentCreate request){
        if(request.patientId()!=null) access.requirePatient(request.patientId());
        var incident=repository.save(Incident.builder().incidentNumber(number()).patientId(request.patientId()).sessionId(request.sessionId())
                .equipmentId(request.equipmentId()).category(request.category()).severity(request.severity()).status("OPEN")
                .occurredAt(request.occurredAt()).description(request.description()).immediateAction(request.immediateAction())
                .causality(request.causality()).reportable(request.reportable()).reportedBy(actor.id()).build());
        audit.record("INCIDENT_REPORTED","CREATE","Incident",incident.getId(),request.patientId(),"{\"severity\":\""+request.severity()+"\"}");
        return incident;
    }

    @Transactional public Incident assign(UUID id,long assigneeId){
        var incident=get(id);var assignee=users.findById(assigneeId).orElseThrow(()->new ResourceNotFoundException("Responsable introuvable."));
        if(!assignee.isEnabled()) throw new BadRequestException("Le responsable est desactive.");
        incident.setAssignedTo(assigneeId);incident.setStatus("UNDER_REVIEW");
        var notification=notifications.save(Notification.builder().recipientId(assigneeId).patientId(incident.getPatientId())
                .notificationType("INCIDENT_ASSIGNED").severity(incident.getSeverity()).title("Incident assigne")
                .message(incident.getDescription()).actionUrl("/incidents/"+id).requiresAcknowledgement(true).build());
        realtime.user(assigneeId,"notification.created",notification.getId(),"{\"incidentId\":\""+id+"\"}");
        audit.record("INCIDENT_ASSIGNED","UPDATE","Incident",id,incident.getPatientId(),"{\"assigneeId\":"+assigneeId+"}");return incident;
    }

    @Transactional public Incident transition(UUID id,IncidentTransition request){
        var incident=get(id);String target=request.status();
        if(target.equals("CLOSED")) return close(incident,request.review());
        if(target.equals("ACTION_REQUIRED") && (request.review()==null||request.review().isBlank())) throw new BadRequestException("L'analyse de cause est obligatoire.");
        if(target.equals("ACTION_REQUIRED")) incident.setRootCause(request.review());
        incident.setStatus(target);audit.record("INCIDENT_STATUS_CHANGED","UPDATE","Incident",id,incident.getPatientId(),"{\"to\":\""+target+"\"}");return incident;
    }

    @Transactional public CorrectiveAction addAction(UUID incidentId,CorrectiveActionCreate request){
        var incident=get(incidentId);
        users.findById(request.ownerId()).orElseThrow(()->new ResourceNotFoundException("Responsable introuvable."));
        var action=actions.save(CorrectiveAction.builder().incidentId(incidentId).actionType(request.actionType()).description(request.description())
                .ownerId(request.ownerId()).dueAt(request.dueAt()).status("OPEN").build());
        incident.setStatus("ACTION_REQUIRED");
        var notification=notifications.save(Notification.builder().recipientId(request.ownerId()).patientId(incident.getPatientId())
                .notificationType("CORRECTIVE_ACTION_ASSIGNED").severity(incident.getSeverity()).title("Action CAPA assignee")
                .message(request.description()).actionUrl("/incidents/"+incidentId).requiresAcknowledgement(true).build());
        realtime.user(request.ownerId(),"notification.created",notification.getId(),"{\"correctiveActionId\":\""+action.getId()+"\"}");
        audit.record("CORRECTIVE_ACTION_CREATED","CREATE","CorrectiveAction",action.getId(),incident.getPatientId(),"{}");return action;
    }

    @Transactional public CorrectiveAction completeAction(UUID incidentId,UUID actionId,CorrectiveActionComplete request){
        var incident=get(incidentId);var action=actions.findById(actionId).orElseThrow(()->new ResourceNotFoundException("Action CAPA introuvable."));
        if(!action.getIncidentId().equals(incidentId)) throw new BadRequestException("Action CAPA incoherente avec l'incident.");
        if(!action.getOwnerId().equals(actor.id())) throw new BadRequestException("Seul le responsable peut terminer cette action.");
        action.setStatus("COMPLETED");action.setCompletedAt(Instant.now());action.setEffectivenessReview(request.effectivenessReview());
        audit.record("CORRECTIVE_ACTION_COMPLETED","UPDATE","CorrectiveAction",actionId,incident.getPatientId(),"{}");return action;
    }

    private Incident close(Incident incident,String review){
        if(review==null||review.isBlank()) throw new BadRequestException("La revue de cloture est obligatoire.");
        if(actions.countByIncidentIdAndStatusNot(incident.getId(),"COMPLETED")>0) throw new BadRequestException("Toutes les actions CAPA doivent etre terminees.");
        if(List.of("HIGH","CRITICAL").contains(incident.getSeverity()) && actions.countByIncidentId(incident.getId())==0) throw new BadRequestException("Une action CAPA est obligatoire pour un incident grave.");
        incident.setStatus("CLOSED");incident.setClosedAt(Instant.now());incident.setClosedBy(actor.id());incident.setClosureReview(review);
        audit.record("INCIDENT_CLOSED","UPDATE","Incident",incident.getId(),incident.getPatientId(),"{}");return incident;
    }

    private String number(){return "INC-"+DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(Instant.now())+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();}
}
