package com.chu.sih.repository;
import com.chu.sih.entity.PatientIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier,UUID>{List<PatientIdentifier> findByPatientIdOrderByIdentifierType(UUID patientId);}
