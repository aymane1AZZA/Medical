package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "incidents")
public class Incident {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String incidentNumber;
    private UUID patientId;
    private UUID sessionId;
    private UUID equipmentId;
    @Column(nullable = false, length = 60) private String category;
    @Column(nullable = false, length = 20) private String severity;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "OPEN";
    @Column(nullable = false) private Instant occurredAt;
    @Column(nullable = false) private Instant detectedAt;
    @Column(nullable = false) private String description;
    private String immediateAction;
    private String causality;
    private String rootCause;
    private String closureReview;
    @Column(nullable = false) private boolean reportable;
    @Column(nullable = false) private Long reportedBy;
    private Long assignedTo;
    private Long closedBy;
    private Instant closedAt;
    @Version private long version;
    @PrePersist void create(){if(detectedAt==null) detectedAt=Instant.now();}
}
