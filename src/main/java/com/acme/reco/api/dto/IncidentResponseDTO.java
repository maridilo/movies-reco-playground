package com.acme.reco.api.dto;

import com.acme.reco.persistence.entity.IncidentEntity;
import com.acme.reco.persistence.entity.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public class IncidentResponseDTO {

    private UUID id;
    private UUID reporterId;
    private UUID technicianId;
    private String title;
    private String description;
    private IncidentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private String resolutionNotes;

    public static IncidentResponseDTO fromEntity(IncidentEntity e) {
        IncidentResponseDTO dto = new IncidentResponseDTO();
        dto.setId(e.getId());
        dto.setReporterId(e.getReporterId());
        dto.setTechnicianId(e.getTechnicianId());
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setStatus(e.getStatus());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setResolvedAt(e.getResolvedAt());
        dto.setResolutionNotes(e.getResolutionNotes());
        return dto;
    }

    // getters y setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(UUID technicianId) {
        this.technicianId = technicianId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
