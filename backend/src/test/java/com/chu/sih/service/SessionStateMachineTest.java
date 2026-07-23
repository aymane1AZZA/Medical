package com.chu.sih.service;

import com.chu.sih.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionStateMachineTest {
    private final SessionStateMachine stateMachine = new SessionStateMachine();

    @Test
    void acceptsTheNominalClinicalPath() {
        assertDoesNotThrow(() -> stateMachine.assertAllowed("PLANNED", "READY", null));
        assertDoesNotThrow(() -> stateMachine.assertAllowed("READY", "IN_PROGRESS", null));
        assertDoesNotThrow(() -> stateMachine.assertAllowed("IN_PROGRESS", "COMPLETED", null));
        assertDoesNotThrow(() -> stateMachine.assertAllowed("COMPLETED", "VALIDATED", null));
    }

    @Test
    void requiresAReasonForSafetyTransitions() {
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("IN_PROGRESS", "PAUSED", null));
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("IN_PROGRESS", "ABORTED", " "));
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("PLANNED", "CANCELLED", null));
    }

    @Test
    void rejectsSkippingAndReopeningTerminalStates() {
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("PLANNED", "IN_PROGRESS", null));
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("VALIDATED", "IN_PROGRESS", null));
        assertThrows(BadRequestException.class,
                () -> stateMachine.assertAllowed("CANCELLED", "PLANNED", null));
    }
}
