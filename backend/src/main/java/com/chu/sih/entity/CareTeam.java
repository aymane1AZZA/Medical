package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="care_teams")
public class CareTeam {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=180) private String name;
    @Column(nullable=false) @Builder.Default private boolean active=true;
    @Column(nullable=false,updatable=false) private Instant createdAt;
    @PrePersist void create(){if(createdAt==null)createdAt=Instant.now();}
}
