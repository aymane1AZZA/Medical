package com.chu.sih.repository;
import com.chu.sih.entity.PatientCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PatientConditionRepository extends JpaRepository<PatientCondition,UUID>{List<PatientCondition> findByPatientIdOrderByRecordedAtDesc(UUID patientId);}
