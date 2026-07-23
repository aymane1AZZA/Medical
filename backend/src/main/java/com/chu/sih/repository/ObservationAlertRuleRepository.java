package com.chu.sih.repository;
import com.chu.sih.entity.ObservationAlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface ObservationAlertRuleRepository extends JpaRepository<ObservationAlertRule, UUID> {
    Optional<ObservationAlertRule> findByObservationCodeAndActiveTrue(String observationCode);
}
