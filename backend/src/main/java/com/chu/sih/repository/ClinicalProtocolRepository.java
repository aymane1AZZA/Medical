package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
public interface ClinicalProtocolRepository extends JpaRepository<ClinicalProtocol, UUID> {
    List<ClinicalProtocol> findByCodeOrderByVersionNumberDesc(String code);
    List<ClinicalProtocol> findByStatusOrderByNameAscVersionNumberDesc(String status);
}
