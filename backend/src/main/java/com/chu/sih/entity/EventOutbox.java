package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "event_outbox")
public class EventOutbox {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 80) private String aggregateType;
    private UUID aggregateId;
    @Column(nullable = false, length = 100) private String eventType;
    private Long recipientId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb") @Builder.Default private String payload = "{}";
    @Column(nullable = false, updatable = false) private Instant occurredAt;
    private Instant publishedAt;
    @PrePersist void create(){if(occurredAt==null) occurredAt=Instant.now();}
}
