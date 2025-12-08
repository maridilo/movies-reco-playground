package com.acme.reco.api.bootstrap;

import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("adminSeederCb")   // nombre de bean único
@Profile("cb")                // solo cuando corre el perfil cb
public class AdminSeeder implements CommandLineRunner {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email:admin@acme.test}")
    private String email;

    @Value("${app.admin.password:admin123}")
    private String password;

    public AdminSeeder(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.existsByEmail(email)) return;
        var u = new AppUser();
        u.setEmail(email);
        u.setName("Admin");
        u.setPasswordHash(encoder.encode(password));
        u.setRole("ADMIN");
        users.save(u);
    }
}
