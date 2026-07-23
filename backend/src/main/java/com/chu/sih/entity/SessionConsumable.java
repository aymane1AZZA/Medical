package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "session_consumables")
public class SessionConsumable {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID sessionId;
    @Column(nullable = false) private UUID inventoryLotId;
    @Column(nullable = false, precision = 14, scale = 3) private BigDecimal quantity;
    @Column(nullable = false) private Long recordedBy;
    @Column(nullable = false, updatable = false) private Instant recordedAt;
    @PrePersist void create(){if(recordedAt==null) recordedAt=Instant.now();}
}
