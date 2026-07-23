package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="patient_conditions")
public class PatientCondition {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    @Column(length=120) private String codeSystem;
    @Column(nullable=false, length=80) private String code;
    @Column(nullable=false) private String display;
    @Column(nullable=false, length=24) @Builder.Default private String clinicalStatus="ACTIVE";
    private Instant onsetAt;
    private Instant abatementAt;
    private Long recordedBy;
    @Column(nullable=false) private Instant recordedAt;
    @PrePersist void create(){if(recordedAt==null)recordedAt=Instant.now();}
}
