package com.chu.sih.repository;
import com.chu.sih.entity.CorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, UUID> {
    List<CorrectiveAction> findByIncidentIdOrderByDueAt(UUID incidentId);
    long countByIncidentId(UUID incidentId);
    long countByIncidentIdAndStatusNot(UUID incidentId, String status);
}
