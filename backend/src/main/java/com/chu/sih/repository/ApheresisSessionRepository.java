package com.chu.sih.repository;
import com.chu.sih.entity.ApheresisSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ApheresisSessionRepository extends JpaRepository<ApheresisSession, UUID> {
    List<ApheresisSession> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    long countByPrescriptionId(UUID prescriptionId);
}
