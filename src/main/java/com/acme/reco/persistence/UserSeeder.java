package com.acme.reco.persistence;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("cb")
@Order(10)
public class UserSeeder implements CommandLineRunner {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public UserSeeder(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override public void run(String... args) {
        if (users.findByEmail("admin@local").isEmpty()) {
            var a = new AppUser();
            a.setEmail("admin@local");
            a.setName("Admin");
            a.setPasswordHash(encoder.encode("admin123"));
            a.setRole("ADMIN");
            users.save(a);
        }
        if (users.findByEmail("user@local").isEmpty()) {
            var u = new AppUser();
            u.setEmail("user@local");
            u.setName("Demo User");
            u.setPasswordHash(encoder.encode("user1234"));
            u.setRole("USER");
            users.save(u);
        }
    }
}
