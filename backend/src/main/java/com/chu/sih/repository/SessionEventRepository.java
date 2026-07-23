package com.chu.sih.repository;
import com.chu.sih.entity.SessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SessionEventRepository extends JpaRepository<SessionEvent, UUID> {
    List<SessionEvent> findBySessionIdOrderByOccurredAt(UUID sessionId);
}
