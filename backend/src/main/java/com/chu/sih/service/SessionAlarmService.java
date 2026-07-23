package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.AlarmResolution;
import com.chu.sih.dto.ClinicalRequests.SessionAlarmCreate;
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
public class SessionAlarmService {
    private final SessionAlarmRepository alarms;
    private final ObservationAlertRuleRepository rules;
    private final ApheresisSessionRepository sessions;
    private final NotificationRepository notifications;
    private final CurrentActor actor;
    private final ClinicalAccessService access;
    private final AuditService audit;
    private final RealTimeEventService realtime;

    @Transactional(readOnly=true) public List<SessionAlarm> list(UUID sessionId){session(sessionId);return alarms.findBySessionIdOrderByRaisedAtDesc(sessionId);}

    @Transactional public SessionAlarm raise(UUID sessionId, SessionAlarmCreate request){
        var session = session(sessionId);
        requireActive(session);
        return create(session, request.deviceId(), request.alarmCode(), request.severity(), request.message(), "MANUAL");
    }

    @Transactional public SessionAlarm acknowledge(UUID sessionId, UUID alarmId){
        var session = session(sessionId);var alarm = alarm(sessionId, alarmId);
        if(alarm.getAcknowledgedAt()==null){alarm.setAcknowledgedAt(Instant.now());alarm.setAcknowledgedBy(actor.id());}
        audit.record("SESSION_ALARM_ACKNOWLEDGED","UPDATE","SessionAlarm",alarmId,session.getPatientId(),"{}");
        realtime.session(sessionId,"session.alarm.acknowledged","{\"alarmId\":\""+alarmId+"\"}");
        return alarm;
    }

    @Transactional public SessionAlarm resolve(UUID sessionId, UUID alarmId, AlarmResolution request){
        var session = session(sessionId);var alarm = alarm(sessionId, alarmId);
        if(alarm.getAcknowledgedAt()==null) throw new BadRequestException("L'alarme doit etre acquittee avant sa resolution.");
        if(alarm.getResolvedAt()!=null) throw new BadRequestException("L'alarme est deja resolue.");
        alarm.setResolvedAt(Instant.now());alarm.setActionTaken(request.actionTaken());
        audit.record("SESSION_ALARM_RESOLVED","UPDATE","SessionAlarm",alarmId,session.getPatientId(),"{}");
        realtime.session(sessionId,"session.alarm.resolved","{\"alarmId\":\""+alarmId+"\"}");
        return alarm;
    }

    @Transactional public SessionAlarm escalate(UUID sessionId, UUID alarmId, long recipientId){
        var session=session(sessionId);var alarm=alarm(sessionId, alarmId);
        if(alarm.getResolvedAt()!=null) throw new BadRequestException("Une alarme resolue ne peut pas etre escaladee.");
        alarm.setEscalatedAt(Instant.now());alarm.setEscalatedTo(recipientId);
        var notification=notifications.save(Notification.builder().recipientId(recipientId).patientId(session.getPatientId())
                .notificationType("SESSION_ALARM_ESCALATED").severity(alarm.getSeverity()).title("Alarme de seance escaladee")
                .message(alarm.getMessage()).actionUrl("/sessions/"+sessionId+"/cockpit").requiresAcknowledgement(true).build());
        realtime.user(recipientId,"notification.created",notification.getId(),"{\"notificationId\":\""+notification.getId()+"\"}");
        audit.record("SESSION_ALARM_ESCALATED","UPDATE","SessionAlarm",alarmId,session.getPatientId(),"{\"recipientId\":"+recipientId+"}");
        return alarm;
    }

    @Transactional public void evaluate(ApheresisSession session, SessionObservation observation){
        if(observation.getValueNumeric()==null) return;
        rules.findByObservationCodeAndActiveTrue(observation.getObservationCode()).ifPresent(rule -> {
            boolean low=rule.getLowerLimit()!=null && observation.getValueNumeric().compareTo(rule.getLowerLimit())<0;
            boolean high=rule.getUpperLimit()!=null && observation.getValueNumeric().compareTo(rule.getUpperLimit())>0;
            if(low||high){
                String message=rule.getDisplay()+" hors seuil: "+observation.getValueNumeric()+" "+(observation.getUnitUcum()==null?"":observation.getUnitUcum());
                create(session, observation.getDeviceId(), "THRESHOLD_"+rule.getObservationCode(), rule.getSeverity(), message, "RULE");
            }
        });
    }

    private SessionAlarm create(ApheresisSession session, UUID deviceId, String code, String severity, String message, String source){
        var alarm=alarms.save(SessionAlarm.builder().sessionId(session.getId()).deviceId(deviceId).alarmCode(code)
                .severity(severity).message(message).raisedAt(Instant.now()).source(source).build());
        audit.record("SESSION_ALARM_RAISED","CREATE","SessionAlarm",alarm.getId(),session.getPatientId(),"{\"severity\":\""+severity+"\"}");
        realtime.session(session.getId(),"session.alarm.raised","{\"alarmId\":\""+alarm.getId()+"\",\"severity\":\""+severity+"\"}");
        return alarm;
    }

    private ApheresisSession session(UUID id){var value=sessions.findById(id).orElseThrow(()->new ResourceNotFoundException("Seance introuvable."));access.requirePatient(value.getPatientId());return value;}
    private SessionAlarm alarm(UUID sessionId,UUID id){var value=alarms.findById(id).orElseThrow(()->new ResourceNotFoundException("Alarme introuvable."));if(!value.getSessionId().equals(sessionId))throw new BadRequestException("Alarme incoherente avec la seance.");return value;}
    private void requireActive(ApheresisSession session){if(!List.of("IN_PROGRESS","PAUSED").contains(session.getStatus()))throw new BadRequestException("Les alarmes sont reservees aux seances actives.");}
}
