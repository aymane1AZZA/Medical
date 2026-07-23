package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="specimens")
public class Specimen {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID orderId;
    @Column(nullable=false,length=80,unique=true) private String accessionNumber;
    @Column(nullable=false,length=120,unique=true) private String barcode;
    @Column(nullable=false,length=80) private String specimenType;
    @Column(nullable=false,length=24) @Builder.Default private String status="EXPECTED";
    private Long collectedBy;
    private Instant collectedAt;
    private Long receivedBy;
    private Instant receivedAt;
    private String rejectionReason;
}
