package com.acme.reco.api.controller;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import com.acme.reco.api.security.JwtService;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthController(AppUserRepository users, PasswordEncoder encoder,
                          AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public record RegisterRequest(@Email @NotBlank String email,
                                  @NotBlank @Size(min=3,max=60) String name,
                                  @NotBlank @Size(min=6,max=120) String password) {}

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) return ResponseEntity.status(409).build();
        var u = new AppUser();
        u.setEmail(req.email());
        u.setName(req.name());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole("USER");
        users.save(u);
        return ResponseEntity.ok().build();
    }

    // --- LOGIN (devuelve token) ---
    public record LoginRequest(@Email @NotBlank String email,
                               @NotBlank String password) {}
    public record LoginResponse(String token) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var principal = (UserDetails) auth.getPrincipal();
        String token = jwt.generateToken(principal);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // --- ME (requiere Authorization: Bearer <token>) ---
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();
        String email = auth.getName();
        var user = users.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()
        ));
    }
}
