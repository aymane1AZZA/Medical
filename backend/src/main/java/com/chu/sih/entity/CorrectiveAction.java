package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "corrective_actions")
public class CorrectiveAction {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID incidentId;
    @Column(nullable = false, length = 24) private String actionType;
    @Column(nullable = false) private String description;
    @Column(nullable = false) private Long ownerId;
    @Column(nullable = false) private Instant dueAt;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "OPEN";
    private Instant completedAt;
    private String effectivenessReview;
}
