package com.chu.sih.repository;
import com.chu.sih.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ConsentRepository extends JpaRepository<Consent, UUID> {
    List<Consent> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    boolean existsByPatientIdAndConsentTypeAndStatus(UUID patientId, String consentType, String status);
}
