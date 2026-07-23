package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "audit_events")
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private Instant eventTime;
    @Column(nullable = false, length = 80) private String eventType;
    @Column(nullable = false, length = 24) private String action;
    @Column(nullable = false, length = 24) private String outcome;
    private Long actorUserId;
    private String actorUsername;
    private String actorRoles;
    private UUID patientId;
    private String entityType;
    private String entityId;
    private String purposeOfUse;
    private String requestId;
    @Column(length = 64) private String ipAddress;
    private String userAgent;
    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    @Builder.Default private String detail = "{}";
    @Column(nullable = false, unique = true) private long chainPosition;
    @Column(nullable = false) @Builder.Default private int hashVersion = 2;
    @Column(length = 64) private String previousHash;
    @Column(nullable = false, length = 64) private String eventHash;
    @PrePersist void create(){if(eventTime==null) eventTime=Instant.now();}
}
