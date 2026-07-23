package com.chu.sih.repository;
import com.chu.sih.entity.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SpecimenRepository extends JpaRepository<Specimen,UUID>{List<Specimen> findByOrderIdOrderByAccessionNumber(UUID orderId);}
