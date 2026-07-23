package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ClinicalMessageRepository extends JpaRepository<ClinicalMessage, UUID> {
    List<ClinicalMessage> findByThreadIdAndDeletedAtIsNullOrderByCreatedAt(UUID threadId);
}
