package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="notification_preferences")
public class NotificationPreference {
    @Id private Long userId;
    @Column(nullable=false) @Builder.Default private boolean inAppEnabled=true;
    @Column(nullable=false) @Builder.Default private boolean emailEnabled=true;
    @Column(nullable=false) private boolean smsEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    @Column(nullable=false,length=60) @Builder.Default private String timezone="Africa/Casablanca";
    @Column(nullable=false) private Instant updatedAt;
    @PrePersist @PreUpdate void update(){updatedAt=Instant.now();}
}
