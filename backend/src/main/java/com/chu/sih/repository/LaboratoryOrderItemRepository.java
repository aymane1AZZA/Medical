package com.chu.sih.repository;
import com.chu.sih.entity.LaboratoryOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface LaboratoryOrderItemRepository extends JpaRepository<LaboratoryOrderItem, UUID> {
    List<LaboratoryOrderItem> findByOrderId(UUID orderId);
    long countByOrderIdAndStatusNot(UUID orderId,String status);
}
