package com.chu.sih.repository;
import com.chu.sih.entity.CriticalResultAcknowledgement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface CriticalResultAcknowledgementRepository extends JpaRepository<CriticalResultAcknowledgement,UUID>{boolean existsByResultId(UUID resultId);List<CriticalResultAcknowledgement> findByResultIdOrderByAcknowledgedAtDesc(UUID resultId);}
