package com.chu.sih.repository;
import com.chu.sih.entity.EpisodeOfCare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface EpisodeOfCareRepository extends JpaRepository<EpisodeOfCare,UUID>{List<EpisodeOfCare> findByPatientIdOrderByStartedAtDesc(UUID patientId);}
