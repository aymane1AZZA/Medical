package com.chu.sih.repository;
import com.chu.sih.entity.PatientAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy,UUID>{List<PatientAllergy> findByPatientIdOrderByRecordedAtDesc(UUID patientId);}
