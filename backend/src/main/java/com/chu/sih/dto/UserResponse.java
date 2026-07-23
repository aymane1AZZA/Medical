package com.chu.sih.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String roleLabel;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
