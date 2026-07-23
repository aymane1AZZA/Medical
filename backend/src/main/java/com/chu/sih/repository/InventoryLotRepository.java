package com.chu.sih.repository;
import com.chu.sih.entity.InventoryLot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
public interface InventoryLotRepository extends JpaRepository<InventoryLot, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from InventoryLot l where l.id = :id")
    Optional<InventoryLot> findByIdForUpdate(@Param("id") UUID id);
    List<InventoryLot> findByItemIdOrderByExpiresOnAsc(UUID itemId);
}
