package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "session_observations")
public class SessionObservation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID sessionId;
    @Column(nullable = false, length = 80) private String observationCode;
    private String codeSystem;
    private BigDecimal valueNumeric;
    private String valueText;
    private String unitUcum;
    @Column(nullable = false, length = 24) @Builder.Default private String source = "MANUAL";
    private UUID deviceId;
    @Column(nullable = false) private Instant observedAt;
    private Long recordedBy;
    @Column(nullable = false, length = 24) @Builder.Default private String validationStatus = "FINAL";
}
