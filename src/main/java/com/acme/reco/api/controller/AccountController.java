package com.acme.reco.api.controller;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class AccountController {

    private final AppUserRepository users;
    private final RatingJpaRepository ratings;
    private final PasswordEncoder encoder;

    public AccountController(AppUserRepository users, RatingJpaRepository ratings, PasswordEncoder encoder) {
        this.users = users;
        this.ratings = ratings;
        this.encoder = encoder;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User principal) {
        var u = users.findByEmail(principal.getUsername()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "name", u.getName(),
                "role", u.getRole()
        ));
    }

    public record UpdateProfile(@NotBlank @Size(min=3,max=60) String name) {}

    @PutMapping
    public ResponseEntity<Void> update(@AuthenticationPrincipal User principal,
                                       @RequestBody UpdateProfile body) {
        var u = users.findByEmail(principal.getUsername()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        u.setName(body.name());
        users.save(u);
        return ResponseEntity.noContent().build();
    }

    public record ChangePassword(@NotBlank String currentPassword,
                                 @NotBlank @Size(min=6,max=120) String newPassword) {}

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User principal,
                                               @RequestBody ChangePassword body) {
        var u = users.findByEmail(principal.getUsername()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        if (!encoder.matches(body.currentPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(403).build();
        }
        u.setPasswordHash(encoder.encode(body.newPassword()));
        users.save(u);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User principal) {
        var u = users.findByEmail(principal.getUsername()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        ratings.deleteAllByIdUserId(u.getId());
        users.delete(u);
        return ResponseEntity.noContent().build();
    }
}
