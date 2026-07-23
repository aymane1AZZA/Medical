package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="patient_reported_outcomes")
public class PatientReportedOutcome {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    private UUID sessionId;
    @Column(nullable=false,length=80) private String questionnaireCode;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String response;
    private BigDecimal score;
    @Column(nullable=false,length=24) @Builder.Default private String status="SUBMITTED";
    @Column(nullable=false,updatable=false) private Instant submittedAt;
    private Long reviewedBy;
    private Instant reviewedAt;
    @PrePersist void create(){if(submittedAt==null)submittedAt=Instant.now();}
}
