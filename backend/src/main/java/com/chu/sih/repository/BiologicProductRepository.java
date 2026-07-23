package com.chu.sih.repository;
import com.chu.sih.entity.BiologicProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface BiologicProductRepository extends JpaRepository<BiologicProduct, UUID> {
    List<BiologicProduct> findBySessionIdOrderByRecordedAtDesc(UUID sessionId);
}
