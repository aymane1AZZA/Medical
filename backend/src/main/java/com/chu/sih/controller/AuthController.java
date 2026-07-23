package com.chu.sih.controller;

import com.chu.sih.dto.AuthResponse;
import com.chu.sih.dto.LoginRequest;
import com.chu.sih.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Value("${app.security.cookies.secure}") private boolean secureCookie;
    @Value("${app.security.cookies.same-site}") private String sameSite;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        var result = authService.login(request, httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"));
        setRefreshCookie(response, result.refreshToken(), 8 * 60 * 60);
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String raw = cookie(request, "apheris_refresh");
        if (raw == null) return ResponseEntity.status(401).build();
        var result = authService.refresh(raw, request.getRemoteAddr(), request.getHeader("User-Agent"));
        setRefreshCookie(response, result.refreshToken(), 8 * 60 * 60);
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(cookie(request, "apheris_refresh"));
        setRefreshCookie(response, "", 0);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() { return ResponseEntity.ok(Map.of("status", "UP")); }

    private void setRefreshCookie(HttpServletResponse response, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from("apheris_refresh", value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(sameSite)
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String cookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) if (name.equals(cookie.getName())) return cookie.getValue();
        return null;
    }
}
