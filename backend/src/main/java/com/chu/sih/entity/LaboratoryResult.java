package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "laboratory_results")
public class LaboratoryResult {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID orderItemId;
    private UUID specimenId;
    @Column(nullable = false, length = 32) private String loincCode;
    private BigDecimal valueNumeric;
    private String valueText;
    private String unitUcum;
    private BigDecimal referenceLow;
    private BigDecimal referenceHigh;
    private String interpretation;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "PRELIMINARY";
    @Column(name = "is_critical", nullable = false) private boolean critical;
    @Column(nullable = false) private Instant measuredAt;
    private Long validatedBy;
    private Instant validatedAt;
    private UUID amendedFromId;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){createdAt=Instant.now();}
}
