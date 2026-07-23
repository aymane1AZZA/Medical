package com.chu.sih.repository;
import com.chu.sih.entity.SessionAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SessionAlarmRepository extends JpaRepository<SessionAlarm, UUID> {
    List<SessionAlarm> findBySessionIdOrderByRaisedAtDesc(UUID sessionId);
    long countBySessionIdAndResolvedAtIsNull(UUID sessionId);
    long countBySessionIdAndSeverityAndResolvedAtIsNull(UUID sessionId, String severity);
}
