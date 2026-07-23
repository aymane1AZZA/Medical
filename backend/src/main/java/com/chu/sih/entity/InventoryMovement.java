package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="inventory_movements")
public class InventoryMovement {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID lotId;
    @Column(nullable=false,length=24) private String movementType;
    @Column(nullable=false,precision=14,scale=3) private BigDecimal quantity;
    @Column(nullable=false,precision=14,scale=3) private BigDecimal quantityBefore;
    @Column(nullable=false,precision=14,scale=3) private BigDecimal quantityAfter;
    @Column(length=60) private String referenceType;
    private UUID referenceId;
    private String reason;
    @Column(nullable=false) private Long recordedBy;
    @Column(nullable=false,updatable=false) private Instant recordedAt;
    @PrePersist void create(){if(recordedAt==null)recordedAt=Instant.now();}
}
