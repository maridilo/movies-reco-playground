package com.acme.reco.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminUpdateUserRoleRequest {

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "USER|ADMIN|TECH", message = "Rol no válido. Debe ser USER, ADMIN o TECH")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
