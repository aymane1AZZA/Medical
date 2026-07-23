package com.chu.sih.repository;
import com.chu.sih.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface InventoryItemRepository extends JpaRepository<InventoryItem,UUID>{List<InventoryItem> findByActiveTrueOrderByName();}
