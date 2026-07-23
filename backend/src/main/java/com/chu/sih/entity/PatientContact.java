package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="patient_contacts")
public class PatientContact {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    @Column(nullable=false, length=180) private String fullName;
    @Column(nullable=false, length=60) private String relationshipCode;
    @Column(length=32) private String phone;
    private String email;
    @Column(name="is_emergency_contact", nullable=false) private boolean emergencyContact;
    @Column(name="is_legal_representative", nullable=false) private boolean legalRepresentative;
}
