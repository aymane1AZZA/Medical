package com.chu.sih.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Size(min = 3, max = 80, message = "Le nom d'utilisateur doit contenir entre 3 et 80 caractères.")
    private String username;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email est invalide.")
    @Size(max = 140, message = "L'email ne doit pas dépasser 140 caractères.")
    private String email;

    @NotBlank(message = "Le nom complet est obligatoire.")
    @Size(max = 140, message = "Le nom complet ne doit pas dépasser 140 caractères.")
    private String fullName;

    @NotBlank(message = "Le rôle est obligatoire.")
    private String role;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre."
    )
    private String password;

    private boolean enabled = true;
}
