package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.NotificationPreferenceUpdate;
import com.chu.sih.entity.Notification;
import com.chu.sih.entity.NotificationPreference;
import com.chu.sih.service.NotificationCenterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinical/notification-center")
@RequiredArgsConstructor
public class NotificationCenterController {
    private final NotificationCenterService center;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Notification> notifications() { return center.notifications(); }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Long> unreadCount() { return Map.of("unread", center.unreadCount()); }

    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public Notification read(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean acknowledge) {
        return center.read(id, acknowledge);
    }

    @GetMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public NotificationPreference preferences() { return center.preferences(); }

    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public NotificationPreference preferences(@Valid @RequestBody NotificationPreferenceUpdate request) {
        return center.updatePreferences(request);
    }
}
