package com.acme.reco.api.security;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CurrentUser {
    private final AppUserRepository users;
    public CurrentUser(AppUserRepository users) { this.users = users; }

    public Optional<AppUser> entity() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getName() == null) return Optional.empty();
        return users.findByEmail(a.getName());
    }

    public Optional<AppUser> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        Object principal = auth.getPrincipal();
        if (principal instanceof AppUser) {
            return Optional.of((AppUser) principal);
        }
        return Optional.empty();
    }

    public Optional<UUID> id() { return entity().map(AppUser::getId); }

    public AppUser getOrThrow() {
        return get().orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
    }
}