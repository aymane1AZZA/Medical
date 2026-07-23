package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "apheresis_sessions")
public class ApheresisSession {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String sessionNumber;
    @Column(nullable = false) private UUID patientId;
    @Column(nullable = false) private UUID prescriptionId;
    private UUID appointmentId;
    private UUID equipmentId;
    private UUID locationId;
    @Column(nullable = false, length = 32) @Builder.Default private String status = "PLANNED";
    @Column(nullable = false) private int sequenceNumber;
    private BigDecimal plannedVolumeMl;
    private BigDecimal actualProcessedVolumeMl;
    private BigDecimal replacementVolumeMl;
    private BigDecimal anticoagulantVolumeMl;
    private BigDecimal fluidBalanceMl;
    private String vascularAccess;
    private Instant startedAt;
    private Instant endedAt;
    private Instant validatedAt;
    private Long validatedBy;
    private String terminationReason;
    private String clinicalSummary;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void create(){var now=Instant.now();createdAt=now;updatedAt=now;}
    @PreUpdate void update(){updatedAt=Instant.now();}
}
