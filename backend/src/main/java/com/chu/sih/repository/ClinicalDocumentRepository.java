package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface ClinicalDocumentRepository extends JpaRepository<ClinicalDocument,UUID>{
    List<ClinicalDocument> findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID patientId);
    Optional<ClinicalDocument> findByIdAndDeletedAtIsNull(UUID id);
}
