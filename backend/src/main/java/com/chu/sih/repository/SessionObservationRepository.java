package com.chu.sih.repository;
import com.chu.sih.entity.SessionObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SessionObservationRepository extends JpaRepository<SessionObservation, UUID> {
    List<SessionObservation> findBySessionIdOrderByObservedAtDesc(UUID sessionId);
}
