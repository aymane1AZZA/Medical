package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID managingOrganizationId;
    private Long portalUserId;
    @Column(nullable = false, unique = true, length = 64) private String medicalRecordNumber;
    @Column(length = 64) private String nationalIdentifier;
    @Column(nullable = false, length = 100) private String familyName;
    @Column(nullable = false, length = 100) private String givenName;
    @Column(nullable = false) private LocalDate birthDate;
    @Column(nullable = false, length = 24) private String administrativeGender;
    @Column(length = 8) private String bloodGroup;
    @Column(length = 32) private String phone;
    private String email;
    @Column(nullable = false, length = 12) @Builder.Default private String preferredLanguage = "fr-MA";
    private Instant deceasedAt;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void create() { var now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void update() { updatedAt = Instant.now(); }
}
