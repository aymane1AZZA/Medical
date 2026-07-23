package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="maintenance_work_orders")
public class MaintenanceWorkOrder {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID equipmentId;
    @Column(nullable=false,unique=true,length=80) private String workOrderNumber;
    @Column(nullable=false,length=32) private String maintenanceType;
    @Column(nullable=false,length=24) @Builder.Default private String status="OPEN";
    @Column(nullable=false,length=16) @Builder.Default private String priority="NORMAL";
    @Column(nullable=false) private String description;
    @Column(nullable=false) private Long openedBy;
    private Long assignedTo;
    @Column(nullable=false,updatable=false) private Instant openedAt;
    private Instant scheduledAt;
    private Instant completedAt;
    private String completionNotes;
    private Instant nextDueAt;
    @Version private long version;
    @PrePersist void create(){if(openedAt==null)openedAt=Instant.now();}
}
