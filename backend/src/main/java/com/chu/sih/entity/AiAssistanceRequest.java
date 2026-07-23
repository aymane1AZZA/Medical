package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="ai_assistance_requests")
public class AiAssistanceRequest {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    private UUID patientId;
    private UUID sessionId;
    @Column(nullable=false,length=40) private String assistanceType;
    @Column(nullable=false) private String purpose;
    private String clinicianInput;
    @Column(nullable=false) private String generatedOutput;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") @Builder.Default private String riskFlags="[]";
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") @Builder.Default private String sourceFacts="{}";
    @Column(nullable=false,length=60) @Builder.Default private String modelProvider="RULE_ENGINE";
    @Column(nullable=false,length=100) @Builder.Default private String modelName="CLINICAL_ASSIST_V1";
    @Column(nullable=false,length=24) @Builder.Default private String status="GENERATED";
    @Column(nullable=false) private Long createdBy;
    @Column(nullable=false,updatable=false) private Instant createdAt;
    private Long reviewedBy;
    private Instant reviewedAt;
    private String reviewNote;
    @PrePersist void create(){if(createdAt==null)createdAt=Instant.now();}
}
