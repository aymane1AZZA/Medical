package com.chu.sih.repository;
import com.chu.sih.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    List<Equipment> findByStatusOrderByAssetNumber(String status);
}
