package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ClinicalTaskRepository extends JpaRepository<ClinicalTask, UUID> {
    List<ClinicalTask> findByOwnerIdAndStatusNotOrderByDueAt(Long ownerId, String status);
}
