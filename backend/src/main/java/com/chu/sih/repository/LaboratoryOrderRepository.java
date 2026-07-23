package com.chu.sih.repository;
import com.chu.sih.entity.LaboratoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface LaboratoryOrderRepository extends JpaRepository<LaboratoryOrder, UUID> {
    List<LaboratoryOrder> findByPatientIdOrderByOrderedAtDesc(UUID patientId);
}
