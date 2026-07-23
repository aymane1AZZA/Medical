package com.chu.sih.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "user_role_assignments")
public class UserRoleAssignment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 60) private String roleCode;
    private UUID organizationId;
    private UUID locationId;
    @Column(nullable = false, length = 24) @Builder.Default private String accessScope = "ORGANIZATION";
    @Column(nullable = false) private Instant validFrom;
    private Instant validUntil;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void create(){Instant now=Instant.now();if(validFrom==null)validFrom=now;createdAt=now;}
}
