package com.chu.sih.repository;
import com.chu.sih.entity.PatientContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PatientContactRepository extends JpaRepository<PatientContact,UUID>{List<PatientContact> findByPatientIdOrderByFullName(UUID patientId);}
