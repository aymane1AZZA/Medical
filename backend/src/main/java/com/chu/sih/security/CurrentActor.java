package com.chu.sih.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentActor {
    public UserPrincipal require() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) return userPrincipal;
        throw new IllegalStateException("Utilisateur authentifié requis.");
    }
    public long id() { return require().getId(); }
}
