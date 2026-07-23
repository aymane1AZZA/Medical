package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "laboratory_orders")
public class LaboratoryOrder {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID patientId;
    private UUID prescriptionId;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "ORDERED";
    @Column(nullable = false, length = 16) @Builder.Default private String priority = "ROUTINE";
    @Column(nullable = false) private Long orderedBy;
    @Column(nullable = false) private Instant orderedAt;
    private Instant requiredAt;
    private String clinicalContext;
    @Version private long version;
    @PrePersist void create(){if(orderedAt==null) orderedAt=Instant.now();}
}
