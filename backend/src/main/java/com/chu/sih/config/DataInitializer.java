package com.chu.sih.config;

import com.chu.sih.entity.Role;
import com.chu.sih.entity.User;
import com.chu.sih.entity.Organization;
import com.chu.sih.entity.UserRoleAssignment;
import com.chu.sih.repository.OrganizationRepository;
import com.chu.sih.repository.UserRoleAssignmentRepository;
import com.chu.sih.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final UserRoleAssignmentRepository assignmentRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.full-name}")
    private String adminFullName;
    @Value("${app.organization.default-code}") private String defaultOrganizationCode;

    @Override
    public void run(String... args) {
        Organization organization = organizationRepository.findByCodeAndActiveTrue(defaultOrganizationCode)
                .orElseGet(() -> organizationRepository.save(Organization.builder()
                        .code(defaultOrganizationCode)
                        .name("CHU Mohammed VI Marrakech")
                        .organizationType("HOSPITAL")
                        .active(true)
                        .build()));
        User admin = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(adminUsername, adminEmail)
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(adminUsername)
                        .email(adminEmail.toLowerCase())
                        .fullName(adminFullName)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .build()));
        if (assignmentRepository.findEffectiveByUserId(admin.getId(), java.time.Instant.now()).isEmpty()) {
            assignmentRepository.save(UserRoleAssignment.builder()
                    .userId(admin.getId())
                    .roleCode(Role.ROLE_ADMIN.name())
                    .organizationId(organization.getId())
                    .accessScope("ORGANIZATION")
                    .active(true)
                    .build());
        }
    }
}
