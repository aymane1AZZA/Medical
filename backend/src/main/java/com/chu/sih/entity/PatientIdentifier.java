package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="patient_identifiers")
public class PatientIdentifier {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    @Column(nullable=false, length=255) private String systemUri;
    @Column(nullable=false, length=128) private String value;
    @Column(nullable=false, length=40) private String identifierType;
    private LocalDate validFrom;
    private LocalDate validUntil;
}
