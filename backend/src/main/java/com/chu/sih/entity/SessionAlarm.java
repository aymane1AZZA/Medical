package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "session_alarms")
public class SessionAlarm {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID sessionId;
    private UUID deviceId;
    @Column(nullable = false, length = 80) private String alarmCode;
    @Column(nullable = false, length = 20) private String severity;
    @Column(nullable = false) private String message;
    @Column(nullable = false) private Instant raisedAt;
    private Instant acknowledgedAt;
    private Long acknowledgedBy;
    private Instant resolvedAt;
    private String actionTaken;
    @Column(nullable = false, length = 24) @Builder.Default private String source = "MANUAL";
    private Instant escalatedAt;
    private Long escalatedTo;
}
