package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "clinical_threads")
public class ClinicalThread {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID patientId;
    private UUID sessionId;
    @Column(nullable = false) private String subject;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "OPEN";
    @Column(nullable = false, length = 16) @Builder.Default private String priority = "ROUTINE";
    @Column(nullable = false) private Long createdBy;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void create(){var now=Instant.now();createdAt=now;updatedAt=now;}
    @PreUpdate void update(){updatedAt=Instant.now();}
}
