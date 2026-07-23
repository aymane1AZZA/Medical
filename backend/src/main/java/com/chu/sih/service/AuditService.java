package com.chu.sih.service;

import com.chu.sih.entity.AuditEvent;
import com.chu.sih.entity.AuditChainHead;
import com.chu.sih.repository.AuditChainHeadRepository;
import com.chu.sih.repository.AuditEventRepository;
import com.chu.sih.security.CurrentActor;
import com.chu.sih.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditEventRepository repository;
    private final AuditChainHeadRepository heads;
    private final CurrentActor currentActor;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public synchronized void record(String eventType, String action, String entityType, Object entityId, UUID patientId, String detail) {
        UserPrincipal actor = currentActor.require();
        AuditChainHead head = heads.findByIdForUpdate((short) 1).orElseGet(() -> heads.saveAndFlush(
                AuditChainHead.builder().id((short) 1).lastPosition(0).updatedAt(Instant.now()).build()));
        String previous = head.getLastHash();
        long position = head.getLastPosition() + 1;
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        String roles = actor.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList().toString();
        String normalizedDetail = normalizeJson(detail);
        String material = String.join("|", "2", Long.toString(position), now.toString(), eventType, action, actor.getUsername(),
                entityType == null ? "" : entityType, entityId == null ? "" : entityId.toString(),
                patientId == null ? "" : patientId.toString(), normalizedDetail,
                previous == null ? "" : previous);
        AuditEvent event = repository.save(AuditEvent.builder()
                .eventTime(now).eventType(eventType).action(action).outcome("SUCCESS")
                .actorUserId(actor.getId()).actorUsername(actor.getUsername()).actorRoles(roles)
                .patientId(patientId).entityType(entityType).entityId(entityId == null ? null : entityId.toString())
                .purposeOfUse("TREATMENT").detail(normalizedDetail).chainPosition(position).hashVersion(2)
                .previousHash(previous).eventHash(sha256(material)).build());
        head.setLastPosition(position);
        head.setLastHash(event.getEventHash());
        head.setUpdatedAt(now);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> verifyIntegrity() {
        String previous = null;
        long expectedPosition = 1;
        for (AuditEvent event : repository.findAllByOrderByChainPositionAsc()) {
            if (event.getChainPosition() != expectedPosition || !java.util.Objects.equals(previous, event.getPreviousHash())) {
                return Map.of("valid", false, "checkedEvents", expectedPosition - 1,
                        "failurePosition", event.getChainPosition(), "reason", "CHAIN_LINK_MISMATCH");
            }
            String expectedHash = event.getHashVersion() == 0 ? event.getEventHash() : expectedHash(event, previous);
            if (!expectedHash.equals(event.getEventHash())) {
                return Map.of("valid", false, "checkedEvents", expectedPosition - 1,
                        "failurePosition", event.getChainPosition(), "reason", "HASH_MISMATCH");
            }
            previous = event.getEventHash();
            expectedPosition++;
        }
        return Map.of("valid", true, "checkedEvents", expectedPosition - 1,
                "headHash", previous == null ? "" : previous);
    }

    private String expectedHash(AuditEvent event, String previous) {
        String normalizedDetail = normalizeJson(event.getDetail());
        if (event.getHashVersion() == 1) {
            return sha256(String.join("|", event.getEventTime().toString(), event.getEventType(), event.getAction(),
                    event.getActorUsername(), nullable(event.getEntityType()), nullable(event.getEntityId()),
                    event.getPatientId() == null ? "" : event.getPatientId().toString(), normalizedDetail,
                    nullable(previous)));
        }
        return sha256(String.join("|", "2", Long.toString(event.getChainPosition()), event.getEventTime().toString(),
                event.getEventType(), event.getAction(), event.getActorUsername(), nullable(event.getEntityType()),
                nullable(event.getEntityId()), event.getPatientId() == null ? "" : event.getPatientId().toString(),
                normalizedDetail, nullable(previous)));
    }

    private String normalizeJson(String value) {
        try {
            return objectMapper.writeValueAsString(objectMapper.readTree(value == null ? "{}" : value));
        } catch (Exception exception) {
            throw new IllegalArgumentException("Le detail d'audit doit etre un JSON valide.", exception);
        }
    }

    private String nullable(String value) { return value == null ? "" : value; }

    private String sha256(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("Impossible de calculer le hash d'audit.", e); }
    }
}
