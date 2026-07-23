package com.chu.sih.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "L'identifiant est obligatoire.")
    private String identifier;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String password;

    private String role;
}
