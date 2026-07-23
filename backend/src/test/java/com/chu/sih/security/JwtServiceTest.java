package com.chu.sih.security;

import com.chu.sih.entity.Role;
import com.chu.sih.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    private static final String SECRET = "test-secret-key-with-at-least-thirty-two-bytes";

    @Test
    void generatesAndValidatesATokenForTheSamePrincipalVersion() {
        JwtService service = new JwtService(SECRET, 60_000, "apheris-test", "apheris-api-test");
        UserPrincipal principal = principal(3);

        String token = service.generateToken(principal);

        assertEquals("clinician", service.extractUsername(token));
        assertTrue(service.isTokenValid(token, principal));
    }

    @Test
    void rejectsATokenAfterTheUserTokenVersionChanges() {
        JwtService service = new JwtService(SECRET, 60_000, "apheris-test", "apheris-api-test");
        String token = service.generateToken(principal(3));

        assertFalse(service.isTokenValid(token, principal(4)));
    }

    @Test
    void rejectsShortSigningSecrets() {
        assertThrows(IllegalArgumentException.class,
                () -> new JwtService("too-short", 60_000, "issuer", "audience"));
    }

    private UserPrincipal principal(int tokenVersion) {
        return new UserPrincipal(User.builder()
                .id(42L)
                .username("clinician")
                .email("clinician@example.test")
                .fullName("Clinician Test")
                .password("encoded")
                .role(Role.ROLE_MEDECIN_SPECIALISTE)
                .enabled(true)
                .tokenVersion(tokenVersion)
                .build());
    }
}
