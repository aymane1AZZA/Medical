package com.chu.sih.entity;

import com.chu.sih.exception.BadRequestException;

import java.util.Arrays;

public enum Role {
    ROLE_ADMIN("Administrateur"),
    ROLE_MEDECIN("Médecin"),
    ROLE_MEDECIN_GENERALISTE("Médecin généraliste"),
    ROLE_MEDECIN_SPECIALISTE("Médecin spécialiste"),
    ROLE_MEDECIN_BIOLOGISTE("Médecin biologiste"),
    ROLE_INFERMIER("Infirmier"),
    ROLE_BIOMEDICAL("Ingénieur biomédical"),
    ROLE_PATIENT("Patient"),
    ROLE_LABO("Personnel de laboratoire");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Role fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Le rôle est obligatoire.");
        }

        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Rôle invalide."));
    }
}
