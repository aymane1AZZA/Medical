package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "session_events")
public class SessionEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID sessionId;
    @Column(nullable = false, length = 60) private String eventType;
    private String fromStatus;
    private String toStatus;
    private String reason;
    @Column(nullable = false) private Instant occurredAt;
    private Long recordedBy;
    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    @Builder.Default private String metadata = "{}";
    @PrePersist void create(){if(occurredAt==null) occurredAt=Instant.now();}
}
