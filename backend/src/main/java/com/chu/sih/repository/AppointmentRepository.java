package com.chu.sih.repository;
import com.chu.sih.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByStartsAtBetweenOrderByStartsAt(Instant from, Instant to);
    List<Appointment> findByPatientIdAndStartsAtAfterOrderByStartsAt(UUID patientId, Instant from);
    boolean existsByEquipmentIdAndStatusNotAndStartsAtLessThanAndEndsAtGreaterThan(UUID equipmentId, String status, Instant endsAt, Instant startsAt);
}
