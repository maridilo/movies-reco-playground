package com.acme.reco.persistence.seed;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile({"cb", "dev"})
public class TechSeeder implements CommandLineRunner {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public TechSeeder(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Optional<AppUser> existing = users.findByEmail("tech@example.com");
        if (existing.isPresent()) {
            return;
        }

        AppUser tech = new AppUser();
        tech.setEmail("tech@example.com");
        tech.setName("Tech User");
        tech.setRole("TECH");
        tech.setPasswordHash(passwordEncoder.encode("password"));

        users.save(tech);
    }
}
