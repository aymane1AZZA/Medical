package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="prescription_requirements")
public class PrescriptionRequirement {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID prescriptionId;
    @Column(nullable=false,length=40) private String requirementType;
    @Column(nullable=false,length=100) private String code;
    @Column(nullable=false) private String display;
    @Column(length=40) private String timing;
    @Column(nullable=false) @Builder.Default private boolean mandatory=true;
    @Column(columnDefinition="jsonb") @ColumnTransformer(write="?::jsonb") private String thresholdDefinition;
}
