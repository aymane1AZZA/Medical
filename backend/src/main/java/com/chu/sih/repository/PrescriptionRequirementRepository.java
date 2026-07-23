package com.chu.sih.repository;
import com.chu.sih.entity.PrescriptionRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PrescriptionRequirementRepository extends JpaRepository<PrescriptionRequirement,UUID>{List<PrescriptionRequirement> findByPrescriptionIdOrderByRequirementTypeAscDisplayAsc(UUID prescriptionId);}
