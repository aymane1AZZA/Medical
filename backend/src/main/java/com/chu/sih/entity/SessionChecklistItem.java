package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="session_checklist_items")
public class SessionChecklistItem {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID sessionId;
    @Column(nullable=false,length=80) private String itemCode;
    @Column(nullable=false) private String label;
    @Column(nullable=false) private boolean mandatory;
    @Column(nullable=false,length=24) @Builder.Default private String status="PENDING";
    private Long completedBy;
    private Instant completedAt;
    private String comment;
}
