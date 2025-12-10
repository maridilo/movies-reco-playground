package com.acme.reco.api.dto;

import com.acme.reco.persistence.entity.AppUser;

import java.util.UUID;

public class AdminUserSummaryDTO {

    private UUID id;
    private String email;
    private String name;
    private String role;

    public static AdminUserSummaryDTO fromEntity(AppUser u) {
        AdminUserSummaryDTO dto = new AdminUserSummaryDTO();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setName(u.getName());
        dto.setRole(u.getRole());
        return dto;
    }

    // getters y setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
