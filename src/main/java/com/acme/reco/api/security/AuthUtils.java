package com.acme.reco.api.security;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthUtils {

    private final AppUserRepository users;

    public AuthUtils(AppUserRepository users) {
        this.users = users;
    }

    /** Devuelve el UUID del usuario autenticado (username = email). */
    public UUID currentUserId() {
        String email = currentUsername()
                .orElseThrow(() -> new AccessDeniedException("No autenticado"));
        AppUser u = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        return u.getId();
    }

    /** Devuelve el email/username del principal actual si existe. */
    public Optional<String> currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String name = auth.getName(); // con tu UserDetailsService, esto es el email
        return Optional.ofNullable(name);
    }
}
