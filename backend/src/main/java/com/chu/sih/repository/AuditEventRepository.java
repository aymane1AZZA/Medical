package com.chu.sih.repository;
import com.chu.sih.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    Optional<AuditEvent> findTopByOrderByEventTimeDesc();
    Page<AuditEvent> findByPatientId(UUID patientId, Pageable pageable);
    List<AuditEvent> findAllByOrderByChainPositionAsc();
}
