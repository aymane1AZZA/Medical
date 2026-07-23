package com.chu.sih.repository;
import com.chu.sih.entity.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface EventOutboxRepository extends JpaRepository<EventOutbox, UUID> {}
