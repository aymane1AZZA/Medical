package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "inventory_lots")
public class InventoryLot {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID itemId;
    @Column(nullable = false, length = 120) private String lotNumber;
    private LocalDate expiresOn;
    @Column(nullable = false, precision = 14, scale = 3) private BigDecimal quantityAvailable;
    private UUID locationId;
    @Column(nullable = false) private boolean quarantined;
}
