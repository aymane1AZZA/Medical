package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "apheresis_prescriptions")
public class ApheresisPrescription {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID patientId;
    private UUID episodeId;
    private UUID protocolId;
    @Column(nullable = false, length = 100) private String indicationCode;
    @Column(nullable = false) private String indicationDisplay;
    @Column(length = 16) private String asfaCategory;
    @Column(nullable = false, length = 60) private String modality;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "DRAFT";
    @Column(nullable = false, length = 16) @Builder.Default private String priority = "ROUTINE";
    @Column(nullable = false) private int sessionsPlanned;
    private String frequencyText;
    private BigDecimal targetVolumeMl;
    @Column(length = 100) private String replacementFluid;
    @Column(length = 100) private String anticoagulant;
    private BigDecimal anticoagulantRatio;
    private String calciumProphylaxis;
    private String premedication;
    private String vascularAccessPlan;
    private String clinicalInstructions;
    @Column(nullable = false) private Long prescribedBy;
    @Column(nullable = false) private Instant prescribedAt;
    private Long validatedBy;
    private Instant validatedAt;
    private String cancelledReason;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void create() { var now=Instant.now(); createdAt=now; updatedAt=now; if(prescribedAt==null) prescribedAt=now; }
    @PreUpdate void update() { updatedAt=Instant.now(); }
}
