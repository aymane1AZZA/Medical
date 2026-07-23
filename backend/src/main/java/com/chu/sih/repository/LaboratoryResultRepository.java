package com.chu.sih.repository;
import com.chu.sih.entity.LaboratoryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface LaboratoryResultRepository extends JpaRepository<LaboratoryResult, UUID> {
    List<LaboratoryResult> findByOrderItemIdOrderByMeasuredAtDesc(UUID orderItemId);
}
