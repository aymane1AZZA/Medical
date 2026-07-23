package com.chu.sih.repository;
import com.chu.sih.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findByStatusNotOrderByOccurredAtDesc(String status);
    List<Incident> findByPatientIdOrderByOccurredAtDesc(UUID patientId);
}
