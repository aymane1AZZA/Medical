package com.chu.sih.service;

import com.chu.sih.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class SessionStateMachine {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "PLANNED", Set.of("READY", "CANCELLED"),
            "READY", Set.of("IN_PROGRESS", "CANCELLED"),
            "IN_PROGRESS", Set.of("PAUSED", "COMPLETED", "ABORTED"),
            "PAUSED", Set.of("IN_PROGRESS", "ABORTED"),
            "COMPLETED", Set.of("VALIDATED"),
            "ABORTED", Set.of("VALIDATED"),
            "CANCELLED", Set.of(),
            "VALIDATED", Set.of()
    );

    public void assertAllowed(String current, String target, String reason) {
        String normalized = target == null ? "" : target.trim().toUpperCase();
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(normalized)) {
            throw new BadRequestException("Transition de séance interdite: " + current + " vers " + normalized + ".");
        }
        if (Set.of("PAUSED", "ABORTED", "CANCELLED").contains(normalized) && (reason == null || reason.isBlank())) {
            throw new BadRequestException("Un motif est obligatoire pour cette transition.");
        }
    }
}
