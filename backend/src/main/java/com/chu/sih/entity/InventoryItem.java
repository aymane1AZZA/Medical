package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="inventory_items")
public class InventoryItem {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false,unique=true,length=80) private String sku;
    @Column(nullable=false) private String name;
    @Column(nullable=false,length=60) private String itemType;
    @Column(nullable=false,length=32) private String unit;
    @Column(nullable=false,precision=14,scale=3) private BigDecimal minimumStock;
    @Column(nullable=false) @Builder.Default private boolean active=true;
}
