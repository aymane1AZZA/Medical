package com.chu.sih.service;

import com.chu.sih.dto.UserRequest;
import com.chu.sih.dto.UserResponse;
import com.chu.sih.dto.UserUpdateRequest;
import com.chu.sih.entity.Role;
import com.chu.sih.entity.User;
import com.chu.sih.entity.UserRoleAssignment;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.mapper.UserMapper;
import com.chu.sih.repository.UserRepository;
import com.chu.sih.repository.OrganizationRepository;
import com.chu.sih.repository.UserRoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.Instant;
import com.chu.sih.security.CurrentActor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentActor currentActor;
    private final OrganizationRepository organizations;
    private final UserRoleAssignmentRepository assignments;
    private final AuditService audit;
    @Value("${app.organization.default-code}") private String defaultOrganizationCode;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(findEntity(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        assertUsernameAvailable(request.getUsername(), null);
        assertEmailAvailable(request.getEmail(), null);

        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .fullName(request.getFullName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.fromValue(request.getRole()))
                .enabled(request.isEnabled())
                .build();

        User saved = userRepository.save(user);
        createAssignment(saved, saved.getRole());
        audit.record("USER_CREATED", "CREATE", "User", saved.getId(), null,
                "{\"role\":\"" + saved.getRole().name() + "\"}");
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findEntity(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            assertUsernameAvailable(request.getUsername(), id);
            user.setUsername(request.getUsername().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            assertEmailAvailable(request.getEmail(), id);
            user.setEmail(request.getEmail().trim().toLowerCase());
        }
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            Role nextRole = Role.fromValue(request.getRole());
            if (user.getRole() != nextRole) {
                assignments.findEffectiveByUserId(user.getId(), Instant.now()).forEach(assignment -> assignment.setActive(false));
                user.setRole(nextRole);
                user.setTokenVersion(user.getTokenVersion() + 1);
                createAssignment(user, nextRole);
            }
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPasswordChangedAt(Instant.now());
            user.setTokenVersion(user.getTokenVersion() + 1);
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        audit.record("USER_UPDATED", "UPDATE", "User", user.getId(), null, "{}");
        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findEntity(id);
        if (currentActor.id() == id) {
            throw new BadRequestException("Vous ne pouvez pas désactiver votre propre compte.");
        }
        if (user.getRole() == Role.ROLE_ADMIN && userRepository.countByRoleAndEnabledTrue(Role.ROLE_ADMIN) <= 1) {
            throw new BadRequestException("Le dernier administrateur actif ne peut pas être désactivé.");
        }
        user.setEnabled(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        assignments.findEffectiveByUserId(user.getId(), Instant.now()).forEach(assignment -> assignment.setActive(false));
        audit.record("USER_DEACTIVATED", "UPDATE", "User", user.getId(), null, "{}");
    }

    private User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private void assertUsernameAvailable(String username, Long currentId) {
        userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, username)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> {
                    throw new BadRequestException("Ce nom d'utilisateur est déjà utilisé.");
                });
    }

    private void assertEmailAvailable(String email, Long currentId) {
        userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(email, email)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> {
                    throw new BadRequestException("Cet email est déjà utilisé.");
                });
    }

    private void createAssignment(User user, Role role) {
        var organization = organizations.findByCodeAndActiveTrue(defaultOrganizationCode)
                .orElseThrow(() -> new IllegalStateException("Organisation par defaut introuvable."));
        assignments.save(UserRoleAssignment.builder()
                .userId(user.getId())
                .roleCode(role.name())
                .organizationId(organization.getId())
                .accessScope(role == Role.ROLE_PATIENT ? "SELF" : "ORGANIZATION")
                .active(true)
                .build());
    }
}
