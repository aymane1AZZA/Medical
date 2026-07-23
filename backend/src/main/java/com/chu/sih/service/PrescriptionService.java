package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.PrescriptionCreate;
import com.chu.sih.entity.ApheresisPrescription;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.ApheresisPrescriptionRepository;
import com.chu.sih.repository.PatientRepository;
import com.chu.sih.repository.ClinicalProtocolRepository;
import com.chu.sih.repository.PrescriptionRequirementRepository;
import com.chu.sih.entity.PrescriptionRequirement;
import com.chu.sih.dto.ClinicalRequests.PrescriptionRequirementCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class PrescriptionService {
    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            "DRAFT", Set.of("SUBMITTED", "CANCELLED"),
            "SUBMITTED", Set.of("VALIDATED", "DRAFT", "CANCELLED"),
            "VALIDATED", Set.of("ACTIVE", "CANCELLED"),
            "ACTIVE", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(), "CANCELLED", Set.of());
    private final ApheresisPrescriptionRepository repository;
    private final PatientRepository patients;
    private final CurrentActor actor;
    private final AuditService audit;
    private final ClinicalAccessService access;
    private final ClinicalProtocolRepository protocols;
    private final PrescriptionRequirementRepository requirements;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true) public ApheresisPrescription get(UUID id){var value=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Prescription introuvable."));access.requirePatient(value.getPatientId());return value;}
    @Transactional(readOnly = true) public List<ApheresisPrescription> forPatient(UUID patientId){access.requirePatient(patientId);return repository.findByPatientIdOrderByPrescribedAtDesc(patientId);}
    @Transactional(readOnly = true) public List<PrescriptionRequirement> requirements(UUID id){get(id);return requirements.findByPrescriptionIdOrderByRequirementTypeAscDisplayAsc(id);}

    @Transactional
    public ApheresisPrescription create(PrescriptionCreate r){
        access.requirePatient(r.patientId());
        if(r.protocolId()!=null){
            var protocol=protocols.findById(r.protocolId()).orElseThrow(()->new ResourceNotFoundException("Protocole introuvable."));
            if(!"ACTIVE".equals(protocol.getStatus()))throw new BadRequestException("Le protocole selectionne n'est pas actif.");
            if(!protocol.getModality().equalsIgnoreCase(r.modality()))throw new BadRequestException("La modalite ne correspond pas au protocole.");
        }
        var prescription = repository.save(ApheresisPrescription.builder()
                .patientId(r.patientId()).episodeId(r.episodeId()).protocolId(r.protocolId())
                .indicationCode(r.indicationCode()).indicationDisplay(r.indicationDisplay()).asfaCategory(r.asfaCategory())
                .modality(r.modality()).priority(r.priority()==null?"ROUTINE":r.priority()).sessionsPlanned(r.sessionsPlanned())
                .frequencyText(r.frequencyText()).targetVolumeMl(r.targetVolumeMl()).replacementFluid(r.replacementFluid())
                .anticoagulant(r.anticoagulant()).anticoagulantRatio(r.anticoagulantRatio())
                .calciumProphylaxis(r.calciumProphylaxis()).premedication(r.premedication())
                .vascularAccessPlan(r.vascularAccessPlan()).clinicalInstructions(r.clinicalInstructions())
                .prescribedBy(actor.id()).status("DRAFT").build());
        audit.record("PRESCRIPTION_CREATED","CREATE","ApheresisPrescription",prescription.getId(),r.patientId(),"{}");
        return prescription;
    }

    @Transactional
    public PrescriptionRequirement addRequirement(UUID prescriptionId,PrescriptionRequirementCreate r){
        var prescription=get(prescriptionId);
        if(!"DRAFT".equals(prescription.getStatus()))throw new BadRequestException("Les exigences ne sont modifiables qu'au brouillon.");
        String threshold=null;
        if(r.thresholdDefinition()!=null&&!r.thresholdDefinition().isBlank()){
            try{threshold=objectMapper.writeValueAsString(objectMapper.readTree(r.thresholdDefinition()));}
            catch(Exception e){throw new BadRequestException("Le seuil doit etre un JSON valide.");}
        }
        var value=requirements.save(PrescriptionRequirement.builder().prescriptionId(prescriptionId)
                .requirementType(r.requirementType().trim().toUpperCase()).code(r.code().trim()).display(r.display().trim())
                .timing(r.timing()==null?null:r.timing().trim()).mandatory(r.mandatory()).thresholdDefinition(threshold).build());
        audit.record("PRESCRIPTION_REQUIREMENT_ADDED","CREATE","PrescriptionRequirement",value.getId(),prescription.getPatientId(),"{}");return value;
    }

    @Transactional
    public ApheresisPrescription transition(UUID id, String target, String reason){
        var prescription=get(id); String normalized=target.toUpperCase();
        if(!TRANSITIONS.getOrDefault(prescription.getStatus(),Set.of()).contains(normalized)) throw new BadRequestException("Transition de prescription interdite.");
        if(normalized.equals("CANCELLED") && (reason==null||reason.isBlank())) throw new BadRequestException("Le motif d'annulation est obligatoire.");
        prescription.setStatus(normalized);
        if(normalized.equals("VALIDATED")){prescription.setValidatedBy(actor.id());prescription.setValidatedAt(Instant.now());}
        if(normalized.equals("CANCELLED")) prescription.setCancelledReason(reason);
        audit.record("PRESCRIPTION_STATUS_CHANGED","UPDATE","ApheresisPrescription",id,prescription.getPatientId(),"{\"to\":\""+normalized+"\"}");
        return prescription;
    }
}
