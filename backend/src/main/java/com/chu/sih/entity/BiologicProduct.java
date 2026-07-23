package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "biologic_products")
public class BiologicProduct {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID sessionId;
    @Column(nullable = false, unique = true, length = 160) private String productIdentifier;
    @Column(nullable = false, length = 80) private String productType;
    @Column(length = 8) private String bloodGroup;
    private BigDecimal volumeMl;
    private Instant expiresAt;
    @Column(nullable = false, length = 40) private String disposition;
    @Column(nullable = false, updatable = false) private Instant recordedAt;
    @PrePersist void create(){if(recordedAt==null) recordedAt=Instant.now();}
}
