package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID patientId;
    private UUID prescriptionId;
    private UUID locationId;
    private UUID equipmentId;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "PROPOSED";
    @Column(nullable = false) private Instant startsAt;
    @Column(nullable = false) private Instant endsAt;
    private String reason;
    @Column(nullable = false) private Long createdBy;
    @Version private long version;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){createdAt=Instant.now();}
}
