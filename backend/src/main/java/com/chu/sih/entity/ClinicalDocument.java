package com.chu.sih.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="clinical_documents")
public class ClinicalDocument {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    private UUID patientId;
    @Column(nullable=false,length=60) private String documentType;
    @Column(nullable=false) private String title;
    @Column(nullable=false,unique=true,length=500) private String storageKey;
    @Column(nullable=false,length=120) private String mimeType;
    @Column(nullable=false) private long sizeBytes;
    @Column(nullable=false,length=64) private String checksumSha256;
    @Column(nullable=false,length=24) @Builder.Default private String status="CURRENT";
    @Column(nullable=false) @Builder.Default private int versionNumber=1;
    private UUID supersedesId;
    private Long authoredBy;
    private Long signedBy;
    private Instant signedAt;
    @Column(nullable=false,updatable=false) private Instant createdAt;
    private String description;
    @Column(nullable=false,length=24) @Builder.Default private String confidentiality="CLINICAL";
    private Instant deletedAt;
    private Long deletedBy;
    @PrePersist void create(){if(createdAt==null)createdAt=Instant.now();}
}
