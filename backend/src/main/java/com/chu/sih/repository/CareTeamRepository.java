package com.chu.sih.repository;
import com.chu.sih.entity.CareTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface CareTeamRepository extends JpaRepository<CareTeam,UUID>{List<CareTeam> findByOrganizationIdAndActiveTrueOrderByName(UUID organizationId);}
