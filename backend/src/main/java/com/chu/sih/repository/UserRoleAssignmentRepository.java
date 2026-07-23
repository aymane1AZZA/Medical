package com.chu.sih.repository;

import com.chu.sih.entity.UserRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserRoleAssignmentRepository extends JpaRepository<UserRoleAssignment, UUID> {
    @Query("select assignment from UserRoleAssignment assignment where assignment.userId = :userId " +
            "and assignment.active = true and assignment.validFrom <= :now " +
            "and (assignment.validUntil is null or assignment.validUntil > :now)")
    List<UserRoleAssignment> findEffectiveByUserId(Long userId, Instant now);
}
