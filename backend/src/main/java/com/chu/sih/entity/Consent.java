package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "consents")
public class Consent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID patientId;
    @Column(nullable = false, length = 60) private String consentType;
    @Column(nullable = false, length = 24) private String status;
    @Column(nullable = false, length = 80) private String scopeCode;
    private Instant grantedAt;
    private Instant validUntil;
    private Instant withdrawnAt;
    private Long recordedBy;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){createdAt=Instant.now();}
}
