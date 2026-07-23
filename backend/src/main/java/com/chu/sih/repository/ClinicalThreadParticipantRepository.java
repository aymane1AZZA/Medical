package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalThreadParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface ClinicalThreadParticipantRepository extends JpaRepository<ClinicalThreadParticipant, UUID> {
    Optional<ClinicalThreadParticipant> findByThreadIdAndUserId(UUID threadId, Long userId);
    List<ClinicalThreadParticipant> findByThreadId(UUID threadId);
    boolean existsByThreadIdAndUserId(UUID threadId, Long userId);
}
