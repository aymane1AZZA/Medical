package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "laboratory_order_items")
public class LaboratoryOrderItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID orderId;
    @Column(nullable = false, length = 32) private String loincCode;
    @Column(nullable = false) private String display;
    private String specimenType;
    @Column(nullable = false, length = 24) @Builder.Default private String status = "ORDERED";
}
