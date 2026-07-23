package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "equipment")
public class Equipment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String assetNumber;
    private String udi;
    @Column(nullable = false, length = 120) private String manufacturer;
    @Column(nullable = false, length = 120) private String model;
    @Column(nullable = false, unique = true, length = 120) private String serialNumber;
    @Column(nullable = false, length = 60) private String equipmentType;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "AVAILABLE";
    private UUID locationId;
    private LocalDate commissionedOn;
    private Instant nextMaintenanceAt;
    private String firmwareVersion;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void create(){var now=Instant.now();createdAt=now;updatedAt=now;}
    @PreUpdate void update(){updatedAt=Instant.now();}
}
