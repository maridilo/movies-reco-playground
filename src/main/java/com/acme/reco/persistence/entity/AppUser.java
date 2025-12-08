package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name="uk_users_email", columnNames = "email"))
public class AppUser {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @Column(nullable=false, length=120)
    private String email;

    @Column(name = "password_hash", nullable=false, length=120)
    private String passwordHash;

    @Column(nullable=false, length=60)
    private String name;

    @Column(nullable=false, length=20)
    private String role = "USER"; // USER / ADMIN

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
