package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "clinical_tasks")
public class ClinicalTask {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID patientId;
    private UUID sessionId;
    @Column(nullable = false, length = 60) private String taskType;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "REQUESTED";
    @Column(nullable = false, length = 16) @Builder.Default private String priority = "ROUTINE";
    @Column(nullable = false) private String description;
    private Long requesterId;
    private Long ownerId;
    private String ownerRole;
    private Instant dueAt;
    private Instant acceptedAt;
    private Instant completedAt;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){createdAt=Instant.now();}
}
