package com.chu.sih.repository;
import com.chu.sih.entity.ClinicalThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;
public interface ClinicalThreadRepository extends JpaRepository<ClinicalThread, UUID> {
    @Query("select t from ClinicalThread t where t.id in (select p.threadId from ClinicalThreadParticipant p where p.userId = :userId) order by t.updatedAt desc")
    List<ClinicalThread> findVisibleTo(@Param("userId") Long userId);
}
