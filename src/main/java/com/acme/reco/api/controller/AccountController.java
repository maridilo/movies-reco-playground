package com.acme.reco.api.controller;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AppUserRepository users;
    private final RatingJpaRepository ratings;
    private final PasswordEncoder encoder;

    public AccountController(AppUserRepository users, RatingJpaRepository ratings, PasswordEncoder encoder) {
        this.users = users;
        this.ratings = ratings;
        this.encoder = encoder;
    }

    public record MeDTO(UUID id, String email, String name, String role) {}
    public record UpdateNameDTO(@NotBlank @Size(min=3,max=60) String name) {}
    public record ChangePasswordDTO(@NotBlank String currentPassword, @NotBlank @Size(min=6,max=120) String newPassword) {}

    @GetMapping("/me")
    public ResponseEntity<MeDTO> me(Authentication auth) {
        var u = users.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(new MeDTO(u.getId(), u.getEmail(), u.getName(), u.getRole()));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateName(Authentication auth, @Valid @RequestBody UpdateNameDTO dto) {
        var u = users.findByEmail(auth.getName()).orElseThrow();
        u.setName(dto.name());
        users.save(u);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordDTO dto) {
        var u = users.findByEmail(auth.getName()).orElseThrow();
        if (!encoder.matches(dto.currentPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        u.setPasswordHash(encoder.encode(dto.newPassword()));
        users.save(u);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Authentication auth) {
        var u = users.findByEmail(auth.getName()).orElseThrow();
        ratings.deleteAllByIdUserId(u.getId());  // asegúrate de tener este método en el repo
        users.deleteById(u.getId());
        return ResponseEntity.noContent().build();
    }
}
