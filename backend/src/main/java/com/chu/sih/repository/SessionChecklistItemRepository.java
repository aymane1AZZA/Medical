package com.chu.sih.repository;
import com.chu.sih.entity.SessionChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SessionChecklistItemRepository extends JpaRepository<SessionChecklistItem,UUID>{
    List<SessionChecklistItem> findBySessionIdOrderByItemCode(UUID sessionId);
    long countBySessionIdAndMandatoryTrueAndStatusNot(UUID sessionId,String status);
}
