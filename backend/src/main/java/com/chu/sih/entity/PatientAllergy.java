package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="patient_allergies")
public class PatientAllergy {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    @Column(nullable=false, length=100) private String substanceCode;
    @Column(nullable=false, length=180) private String substanceDisplay;
    @Column(nullable=false, length=20) private String criticality;
    private String reaction;
    @Column(nullable=false, length=20) @Builder.Default private String clinicalStatus="ACTIVE";
    private Long recordedBy;
    @Column(nullable=false) private Instant recordedAt;
    @PrePersist void create(){if(recordedAt==null)recordedAt=Instant.now();}
}
