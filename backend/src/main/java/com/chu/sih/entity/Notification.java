package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private Long recipientId;
    private UUID patientId;
    @Column(nullable = false, length = 60) private String notificationType;
    @Column(nullable = false, length = 20) @Builder.Default private String severity = "INFO";
    @Column(nullable = false) private String title;
    @Column(nullable = false) private String message;
    private String actionUrl;
    @Column(nullable = false) private boolean requiresAcknowledgement;
    private Instant acknowledgedAt;
    private Instant readAt;
    private Instant expiresAt;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){createdAt=Instant.now();}
}
