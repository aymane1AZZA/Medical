package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "observation_alert_rules")
public class ObservationAlertRule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String observationCode;
    @Column(nullable = false, length = 160) private String display;
    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    @Column(nullable = false, length = 20) private String severity;
    @Column(length = 40) private String unitUcum;
    @Column(nullable = false) private boolean active;
}
