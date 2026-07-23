package com.chu.sih.service;

import com.chu.sih.dto.PatientRecordRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class PatientRecordService {
    private final PatientIdentifierRepository identifiers;
    private final PatientContactRepository contacts;
    private final PatientAllergyRepository allergies;
    private final PatientConditionRepository conditions;
    private final EpisodeOfCareRepository episodes;
    private final ClinicalAccessService access;
    private final CurrentActor actor;
    private final AuditService audit;

    @Transactional(readOnly=true) public List<PatientIdentifier> identifiers(UUID patientId){access.requirePatient(patientId);return identifiers.findByPatientIdOrderByIdentifierType(patientId);}
    @Transactional(readOnly=true) public List<PatientContact> contacts(UUID patientId){access.requirePatient(patientId);return contacts.findByPatientIdOrderByFullName(patientId);}
    @Transactional(readOnly=true) public List<PatientAllergy> allergies(UUID patientId){access.requirePatient(patientId);return allergies.findByPatientIdOrderByRecordedAtDesc(patientId);}
    @Transactional(readOnly=true) public List<PatientCondition> conditions(UUID patientId){access.requirePatient(patientId);return conditions.findByPatientIdOrderByRecordedAtDesc(patientId);}
    @Transactional(readOnly=true) public List<EpisodeOfCare> episodes(UUID patientId){access.requirePatient(patientId);return episodes.findByPatientIdOrderByStartedAtDesc(patientId);}

    @Transactional public PatientIdentifier addIdentifier(UUID patientId,IdentifierCreate r){
        access.requirePatient(patientId);
        var value=identifiers.save(PatientIdentifier.builder().patientId(patientId).systemUri(r.systemUri().trim())
                .value(r.value().trim()).identifierType(r.identifierType().trim()).validFrom(r.validFrom()).validUntil(r.validUntil()).build());
        audit.record("PATIENT_IDENTIFIER_ADDED","CREATE","PatientIdentifier",value.getId(),patientId,"{}");return value;
    }
    @Transactional public PatientContact addContact(UUID patientId,ContactCreate r){
        access.requirePatient(patientId);
        var value=contacts.save(PatientContact.builder().patientId(patientId).fullName(r.fullName().trim())
                .relationshipCode(r.relationshipCode().trim()).phone(blank(r.phone())).email(blank(r.email()))
                .emergencyContact(r.emergencyContact()).legalRepresentative(r.legalRepresentative()).build());
        audit.record("PATIENT_CONTACT_ADDED","CREATE","PatientContact",value.getId(),patientId,"{}");return value;
    }
    @Transactional public PatientAllergy addAllergy(UUID patientId,AllergyCreate r){
        access.requirePatient(patientId);
        var value=allergies.save(PatientAllergy.builder().patientId(patientId).substanceCode(r.substanceCode().trim())
                .substanceDisplay(r.substanceDisplay().trim()).criticality(r.criticality()).reaction(blank(r.reaction()))
                .clinicalStatus("ACTIVE").recordedBy(actor.id()).build());
        audit.record("PATIENT_ALLERGY_ADDED","CREATE","PatientAllergy",value.getId(),patientId,
                "{\"criticality\":\""+r.criticality()+"\"}");return value;
    }
    @Transactional public PatientAllergy resolveAllergy(UUID patientId,UUID allergyId,String reason){
        requireReason(reason);access.requirePatient(patientId);
        var value=allergies.findById(allergyId).orElseThrow(()->new ResourceNotFoundException("Allergie introuvable."));
        assertPatient(patientId,value.getPatientId());value.setClinicalStatus("RESOLVED");
        audit.record("PATIENT_ALLERGY_RESOLVED","UPDATE","PatientAllergy",allergyId,patientId,"{}");return value;
    }
    @Transactional public PatientCondition addCondition(UUID patientId,ConditionCreate r){
        access.requirePatient(patientId);
        var value=conditions.save(PatientCondition.builder().patientId(patientId).codeSystem(blank(r.codeSystem()))
                .code(r.code().trim()).display(r.display().trim()).clinicalStatus("ACTIVE").onsetAt(r.onsetAt())
                .recordedBy(actor.id()).build());
        audit.record("PATIENT_CONDITION_ADDED","CREATE","PatientCondition",value.getId(),patientId,"{}");return value;
    }
    @Transactional public PatientCondition resolveCondition(UUID patientId,UUID conditionId,String reason){
        requireReason(reason);access.requirePatient(patientId);
        var value=conditions.findById(conditionId).orElseThrow(()->new ResourceNotFoundException("Probleme medical introuvable."));
        assertPatient(patientId,value.getPatientId());value.setClinicalStatus("RESOLVED");value.setAbatementAt(Instant.now());
        audit.record("PATIENT_CONDITION_RESOLVED","UPDATE","PatientCondition",conditionId,patientId,"{}");return value;
    }
    @Transactional public EpisodeOfCare openEpisode(UUID patientId,EpisodeCreate r){
        var patient=access.requirePatient(patientId);
        var value=episodes.save(EpisodeOfCare.builder().patientId(patientId).careTeamId(r.careTeamId()).status("ACTIVE")
                .startedAt(r.startedAt()).managingOrganizationId(patient.getManagingOrganizationId()).build());
        audit.record("EPISODE_OF_CARE_OPENED","CREATE","EpisodeOfCare",value.getId(),patientId,"{}");return value;
    }
    @Transactional public EpisodeOfCare closeEpisode(UUID patientId,UUID episodeId,String reason){
        requireReason(reason);access.requirePatient(patientId);
        var value=episodes.findById(episodeId).orElseThrow(()->new ResourceNotFoundException("Episode de soins introuvable."));
        assertPatient(patientId,value.getPatientId());
        if(!"ACTIVE".equals(value.getStatus()))throw new BadRequestException("Seul un episode actif peut etre cloture.");
        value.setStatus("FINISHED");value.setEndedAt(Instant.now());
        audit.record("EPISODE_OF_CARE_CLOSED","UPDATE","EpisodeOfCare",episodeId,patientId,"{}");return value;
    }

    private void assertPatient(UUID expected,UUID actual){if(!expected.equals(actual))throw new BadRequestException("Ressource rattachee a un autre patient.");}
    private void requireReason(String reason){if(reason==null||reason.isBlank())throw new BadRequestException("Le motif est obligatoire.");}
    private String blank(String value){return value==null||value.isBlank()?null:value.trim();}
}
