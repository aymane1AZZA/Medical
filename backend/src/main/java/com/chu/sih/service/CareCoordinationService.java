package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.ConsentCreate;
import com.chu.sih.dto.ClinicalRequests.TaskCreate;
import com.chu.sih.entity.ClinicalTask;
import com.chu.sih.entity.Consent;
import com.chu.sih.entity.Notification;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.ClinicalTaskRepository;
import com.chu.sih.repository.ConsentRepository;
import com.chu.sih.repository.NotificationRepository;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CareCoordinationService {
    private final ConsentRepository consents;
    private final ClinicalTaskRepository tasks;
    private final NotificationRepository notifications;
    private final CurrentActor actor;
    private final AuditService audit;
    private final ClinicalAccessService access;

    @Transactional(readOnly=true) public List<Consent> consents(UUID patientId){access.requirePatient(patientId);return consents.findByPatientIdOrderByCreatedAtDesc(patientId);}
    @Transactional public Consent grant(ConsentCreate r){
        access.requirePatient(r.patientId());
        var consent=consents.save(Consent.builder().patientId(r.patientId()).consentType(r.consentType()).scopeCode(r.scopeCode())
                .status("ACTIVE").grantedAt(Instant.now()).validUntil(r.validUntil()).recordedBy(actor.id()).build());
        audit.record("CONSENT_RECORDED","CREATE","Consent",consent.getId(),r.patientId(),"{}");return consent;
    }
    @Transactional public Consent withdraw(UUID id,String reason){
        if(reason==null||reason.isBlank()) throw new BadRequestException("Le motif de retrait est obligatoire.");
        var consent=consents.findById(id).orElseThrow(()->new ResourceNotFoundException("Consentement introuvable."));
        access.requirePatient(consent.getPatientId());
        consent.setStatus("WITHDRAWN");consent.setWithdrawnAt(Instant.now());
        audit.record("CONSENT_WITHDRAWN","UPDATE","Consent",id,consent.getPatientId(),"{}");return consent;
    }
    @Transactional public ClinicalTask createTask(TaskCreate r){
        if(r.patientId()!=null) access.requirePatient(r.patientId());
        var task=tasks.save(ClinicalTask.builder().patientId(r.patientId()).sessionId(r.sessionId()).taskType(r.taskType())
                .priority(r.priority()==null?"ROUTINE":r.priority()).description(r.description()).requesterId(actor.id())
                .ownerId(r.ownerId()).ownerRole(r.ownerRole()).dueAt(r.dueAt()).status("REQUESTED").build());
        if(r.ownerId()!=null) notifications.save(Notification.builder().recipientId(r.ownerId()).patientId(r.patientId())
                .notificationType("TASK_ASSIGNED").severity(r.priority()==null?"INFO":r.priority()).title("Nouvelle tâche clinique")
                .message(r.description()).actionUrl("/tasks/"+task.getId()).build());
        audit.record("CLINICAL_TASK_CREATED","CREATE","ClinicalTask",task.getId(),r.patientId(),"{}");return task;
    }
    @Transactional(readOnly=true) public List<Notification> notifications(){return notifications.findByRecipientIdOrderByCreatedAtDesc(actor.id());}
    @Transactional public Notification readNotification(UUID id,boolean acknowledge){
        var n=notifications.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification introuvable."));
        if(!n.getRecipientId().equals(actor.id())) throw new BadRequestException("Cette notification ne vous appartient pas.");
        n.setReadAt(Instant.now());if(acknowledge)n.setAcknowledgedAt(Instant.now());return n;
    }
}
