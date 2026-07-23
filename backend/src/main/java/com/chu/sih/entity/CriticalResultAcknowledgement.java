package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="critical_result_acknowledgements")
public class CriticalResultAcknowledgement {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID resultId;
    @Column(nullable=false) private Long acknowledgedBy;
    @Column(nullable=false) private Instant acknowledgedAt;
    @Column(nullable=false) private String actionTaken;
    @PrePersist void create(){if(acknowledgedAt==null)acknowledgedAt=Instant.now();}
}
