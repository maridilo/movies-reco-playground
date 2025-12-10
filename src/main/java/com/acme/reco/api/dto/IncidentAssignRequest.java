package com.acme.reco.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class IncidentAssignRequest {

    @NotNull
    private UUID technicianId;

    public UUID getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(UUID technicianId) {
        this.technicianId = technicianId;
    }
}
