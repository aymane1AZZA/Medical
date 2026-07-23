package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "clinical_thread_participants")
public class ClinicalThreadParticipant {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID threadId;
    @Column(nullable = false) private Long userId;
    @Column(length = 60) private String participantRole;
    @Column(nullable = false, updatable = false) private Instant joinedAt;
    private Instant lastReadAt;
    @Column(nullable = false) private boolean muted;
    @PrePersist void create(){if(joinedAt==null) joinedAt=Instant.now();}
}
