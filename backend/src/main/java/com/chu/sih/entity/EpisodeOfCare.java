package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="episodes_of_care")
public class EpisodeOfCare {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID patientId;
    private UUID careTeamId;
    @Column(nullable=false, length=24) private String status;
    @Column(nullable=false) private Instant startedAt;
    private Instant endedAt;
    private UUID managingOrganizationId;
    @Version private long version;
}
