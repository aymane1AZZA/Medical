package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "clinical_protocols")
public class ClinicalProtocol {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 60) private String code;
    @Column(nullable = false) private String name;
    @Column(nullable = false, length = 60) private String modality;
    @Column(nullable = false) private int versionNumber;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "DRAFT";
    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    @Builder.Default private String definition = "{}";
    private LocalDate effectiveFrom;
    private LocalDate effectiveUntil;
    private Long approvedBy;
    private Instant approvedAt;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create() { createdAt = Instant.now(); }
}
