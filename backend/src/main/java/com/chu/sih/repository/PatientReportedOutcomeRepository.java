package com.chu.sih.repository;
import com.chu.sih.entity.PatientReportedOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PatientReportedOutcomeRepository extends JpaRepository<PatientReportedOutcome,UUID>{List<PatientReportedOutcome> findByPatientIdOrderBySubmittedAtDesc(UUID patientId);}
