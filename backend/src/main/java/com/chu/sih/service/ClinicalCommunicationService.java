package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.ClinicalMessageCreate;
import com.chu.sih.dto.ClinicalRequests.ClinicalThreadCreate;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ClinicalCommunicationService {
    private final ClinicalThreadRepository threads;
    private final ClinicalThreadParticipantRepository participants;
    private final ClinicalMessageRepository messages;
    private final ApheresisSessionRepository sessions;
    private final UserRepository users;
    private final NotificationRepository notifications;
    private final CurrentActor actor;
    private final ClinicalAccessService access;
    private final AuditService audit;
    private final RealTimeEventService realtime;

    @Transactional(readOnly=true) public List<ClinicalThread> list(){return threads.findVisibleTo(actor.id());}
    @Transactional(readOnly=true) public ClinicalThread get(UUID id){requireParticipant(id);return threads.findById(id).orElseThrow(()->new ResourceNotFoundException("Fil clinique introuvable."));}
    @Transactional(readOnly=true) public List<ClinicalMessage> messages(UUID threadId){requireParticipant(threadId);return messages.findByThreadIdAndDeletedAtIsNullOrderByCreatedAt(threadId);}

    @Transactional public ClinicalThread create(ClinicalThreadCreate request){
        UUID patientId=request.patientId();
        if(request.sessionId()!=null){
            var session=sessions.findById(request.sessionId()).orElseThrow(()->new ResourceNotFoundException("Seance introuvable."));
            if(patientId!=null && !patientId.equals(session.getPatientId())) throw new BadRequestException("Le patient ne correspond pas a la seance.");
            patientId=session.getPatientId();
        }
        access.requirePatient(patientId);
        var thread=threads.save(ClinicalThread.builder().patientId(patientId).sessionId(request.sessionId()).subject(request.subject())
                .priority(request.priority()==null?"ROUTINE":request.priority()).createdBy(actor.id()).build());
        var memberIds=new LinkedHashSet<>(request.participantIds());memberIds.add(actor.id());
        for(Long userId:memberIds){
            var user=users.findById(userId).orElseThrow(()->new ResourceNotFoundException("Participant introuvable: "+userId));
            if(!user.isEnabled()) throw new BadRequestException("Un participant est desactive: "+userId);
            participants.save(ClinicalThreadParticipant.builder().threadId(thread.getId()).userId(userId).build());
        }
        postInternal(thread, new ClinicalMessageCreate(null,"TEXT",thread.getPriority(),request.initialMessage()));
        audit.record("CLINICAL_THREAD_CREATED","CREATE","ClinicalThread",thread.getId(),patientId,"{}");
        return thread;
    }

    @Transactional public ClinicalMessage post(UUID threadId, ClinicalMessageCreate request){
        var thread=get(threadId);
        if(!thread.getStatus().equals("OPEN")) throw new BadRequestException("Ce fil clinique n'accepte plus de messages.");
        return postInternal(thread, request);
    }

    @Transactional public ClinicalThread resolve(UUID threadId){
        var thread=get(threadId);thread.setStatus("RESOLVED");
        audit.record("CLINICAL_THREAD_RESOLVED","UPDATE","ClinicalThread",threadId,thread.getPatientId(),"{}");return thread;
    }

    @Transactional public void markRead(UUID threadId){
        var participant=requireParticipant(threadId);participant.setLastReadAt(Instant.now());
    }

    private ClinicalMessage postInternal(ClinicalThread thread, ClinicalMessageCreate request){
        if(request.replyToId()!=null){
            var parent=messages.findById(request.replyToId()).orElseThrow(()->new ResourceNotFoundException("Message parent introuvable."));
            if(!parent.getThreadId().equals(thread.getId())) throw new BadRequestException("Le message parent appartient a un autre fil.");
        }
        var message=messages.save(ClinicalMessage.builder().threadId(thread.getId()).senderId(actor.id()).replyToId(request.replyToId())
                .messageType(request.messageType()==null?"TEXT":request.messageType()).urgency(request.urgency()==null?"ROUTINE":request.urgency())
                .body(request.body()).build());
        thread.setUpdatedAt(Instant.now());
        for(var participant:participants.findByThreadId(thread.getId())){
            if(participant.getUserId().equals(actor.id())||participant.isMuted()) continue;
            var notification=notifications.save(Notification.builder().recipientId(participant.getUserId()).patientId(thread.getPatientId())
                    .notificationType("CLINICAL_MESSAGE").severity(message.getUrgency()).title(thread.getSubject())
                    .message(message.getBody()).actionUrl("/communications/"+thread.getId())
                    .requiresAcknowledgement(message.getUrgency().equals("STAT")).build());
            realtime.user(participant.getUserId(),"clinical.message.created",notification.getId(),"{\"threadId\":\""+thread.getId()+"\"}");
        }
        audit.record("CLINICAL_MESSAGE_SENT","CREATE","ClinicalMessage",message.getId(),thread.getPatientId(),"{\"threadId\":\""+thread.getId()+"\"}");
        return message;
    }

    private ClinicalThreadParticipant requireParticipant(UUID threadId){
        return participants.findByThreadIdAndUserId(threadId,actor.id()).orElseThrow(()->new BadRequestException("Vous ne participez pas a ce fil clinique."));
    }
}
