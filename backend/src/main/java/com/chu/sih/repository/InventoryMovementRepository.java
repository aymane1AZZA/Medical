package com.chu.sih.repository;
import com.chu.sih.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,UUID>{List<InventoryMovement> findByLotIdOrderByRecordedAtDesc(UUID lotId);}
