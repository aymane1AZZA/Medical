package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.NotificationPreferenceUpdate;
import com.chu.sih.entity.Notification;
import com.chu.sih.entity.NotificationPreference;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.NotificationPreferenceRepository;
import com.chu.sih.repository.NotificationRepository;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationCenterService {
    private final NotificationRepository notifications;
    private final NotificationPreferenceRepository preferences;
    private final CurrentActor actor;

    @Transactional(readOnly = true)
    public List<Notification> notifications() {
        return notifications.findByRecipientIdOrderByCreatedAtDesc(actor.id());
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return notifications.countByRecipientIdAndReadAtIsNull(actor.id());
    }

    @Transactional
    public Notification read(UUID id, boolean acknowledge) {
        var notification = notifications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification introuvable."));
        if (!notification.getRecipientId().equals(actor.id())) throw new BadRequestException("Cette notification ne vous appartient pas.");
        notification.setReadAt(Instant.now());
        if (acknowledge) notification.setAcknowledgedAt(Instant.now());
        return notification;
    }

    @Transactional(readOnly = true)
    public NotificationPreference preferences() {
        return preferences.findById(actor.id()).orElseGet(() -> NotificationPreference.builder().userId(actor.id()).build());
    }

    @Transactional
    public NotificationPreference updatePreferences(NotificationPreferenceUpdate request) {
        var preference = preferences.findById(actor.id()).orElseGet(() -> NotificationPreference.builder().userId(actor.id()).build());
        preference.setInAppEnabled(request.inAppEnabled());
        preference.setEmailEnabled(request.emailEnabled());
        preference.setSmsEnabled(request.smsEnabled());
        preference.setQuietHoursStart(request.quietHoursStart());
        preference.setQuietHoursEnd(request.quietHoursEnd());
        preference.setTimezone(request.timezone());
        return preferences.save(preference);
    }
}
