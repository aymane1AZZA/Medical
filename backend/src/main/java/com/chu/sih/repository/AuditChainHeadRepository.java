package com.chu.sih.repository;

import com.chu.sih.entity.AuditChainHead;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuditChainHeadRepository extends JpaRepository<AuditChainHead, Short> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select head from AuditChainHead head where head.id = :id")
    Optional<AuditChainHead> findByIdForUpdate(Short id);
}
