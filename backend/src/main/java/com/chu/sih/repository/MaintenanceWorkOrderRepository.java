package com.chu.sih.repository;
import com.chu.sih.entity.MaintenanceWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface MaintenanceWorkOrderRepository extends JpaRepository<MaintenanceWorkOrder,UUID>{
    List<MaintenanceWorkOrder> findByEquipmentIdOrderByOpenedAtDesc(UUID equipmentId);
    boolean existsByEquipmentIdAndStatusIn(UUID equipmentId,List<String> statuses);
}
