package com.chu.sih.repository;
import com.chu.sih.entity.SessionConsumable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SessionConsumableRepository extends JpaRepository<SessionConsumable, UUID> {
    List<SessionConsumable> findBySessionIdOrderByRecordedAtDesc(UUID sessionId);
}
