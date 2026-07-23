package com.chu.sih.service;

import com.chu.sih.dto.AuthResponse;
import com.chu.sih.dto.LoginRequest;
import com.chu.sih.dto.LoginResult;
import com.chu.sih.entity.Role;
import com.chu.sih.entity.User;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.mapper.UserMapper;
import com.chu.sih.repository.UserRepository;
import com.chu.sih.security.JwtService;
import com.chu.sih.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokens;

    @Value("${app.security.login.max-attempts}") private int maxAttempts;
    @Value("${app.security.login.lock-duration-minutes}") private long lockMinutes;

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public LoginResult login(LoginRequest request, String ip, String userAgent) {
        User user = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("Identifiants incorrects."));
        if (!user.isEnabled()) throw new DisabledException("Compte désactivé.");
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now()))
            throw new BadCredentialsException("Compte temporairement verrouillé.");

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));
        } catch (BadCredentialsException exception) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= maxAttempts)
                user.setLockedUntil(Instant.now().plusSeconds(lockMinutes * 60));
            userRepository.save(user);
            throw exception;
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            Role selectedRole = Role.fromValue(request.getRole());
            if (user.getRole() != selectedRole)
                throw new BadRequestException("Le rôle sélectionné ne correspond pas au compte.");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(Instant.now());
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        var refresh = refreshTokens.issue(user, null, ip, userAgent);
        return new LoginResult(response(user, jwtService.generateToken(principal)), refresh.raw());
    }

    @Transactional
    public LoginResult refresh(String raw, String ip, String userAgent) {
        var used = refreshTokens.consume(raw);
        User user = userRepository.findById(used.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Compte introuvable."));
        if (!user.isEnabled()) throw new DisabledException("Compte désactivé.");
        var replacement = refreshTokens.issue(user, used.getFamilyId(), ip, userAgent);
        refreshTokens.linkReplacement(used, replacement.entity());
        return new LoginResult(response(user, jwtService.generateToken(new UserPrincipal(user))), replacement.raw());
    }

    @Transactional
    public void logout(String raw) {
        if (raw != null && !raw.isBlank()) refreshTokens.revoke(raw);
    }

    private AuthResponse response(User user, String token) {
        return AuthResponse.builder().token(token).tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs() / 1000).user(userMapper.toResponse(user)).build();
    }
}
