package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "clinical_messages")
public class ClinicalMessage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID threadId;
    @Column(nullable = false) private Long senderId;
    private UUID replyToId;
    @Column(nullable = false, length = 24) @Builder.Default private String messageType = "TEXT";
    @Column(nullable = false, length = 16) @Builder.Default private String urgency = "ROUTINE";
    @Column(nullable = false) private String body;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    private Instant editedAt;
    private Instant deletedAt;
    @PrePersist void create(){if(createdAt==null) createdAt=Instant.now();}
}
