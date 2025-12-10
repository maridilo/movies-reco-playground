package com.acme.reco.api;

import com.acme.reco.api.dto.*;
import com.acme.reco.domain.service.IncidentService;
import com.acme.reco.persistence.entity.IncidentEntity;
import com.acme.reco.persistence.entity.IncidentStatus;
import com.acme.reco.api.security.CurrentUser;
import com.acme.reco.persistence.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidents;
    private final CurrentUser currentUser;

    public IncidentController(IncidentService incidents, CurrentUser currentUser) {
        this.incidents = incidents;
        this.currentUser = currentUser;
    }

    /**
     * Crea una incidencia por parte de cualquier usuario autenticado.
     */
    @PostMapping
    public ResponseEntity<IncidentResponseDTO> create(@Valid @RequestBody IncidentCreateRequest req) {
        AppUser user = currentUser.getOrThrow();
        IncidentEntity e = incidents.create(user.getId(), req.getTitle(), req.getDescription());
        return ResponseEntity.ok(IncidentResponseDTO.fromEntity(e));
    }

    /**
     * Lista incidencias (solo técnicos o admins).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECH')")
    public Page<IncidentResponseDTO> list(
            @RequestParam(value = "status", required = false) IncidentStatus status,
            @RequestParam(value = "technicianId", required = false) UUID technicianId,
            @RequestParam(value = "reporterId", required = false) UUID reporterId,
            Pageable pageable
    ) {
        Page<IncidentEntity> page = incidents.search(
                Optional.ofNullable(status),
                Optional.ofNullable(technicianId),
                Optional.ofNullable(reporterId),
                pageable
        );
        return page.map(IncidentResponseDTO::fromEntity);
    }

    /**
     * Lista incidencias del usuario actual (reporter).
     */
    @GetMapping("/me")
    public Page<IncidentResponseDTO> myIncidents(Pageable pageable) {
        AppUser user = currentUser.getOrThrow();
        Page<IncidentEntity> page = incidents.search(
                Optional.empty(),
                Optional.empty(),
                Optional.of(user.getId()),
                pageable
        );
        return page.map(IncidentResponseDTO::fromEntity);
    }

    /**
     * Asigna una incidencia a un técnico (solo técnicos o admins).
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','TECH')")
    public IncidentResponseDTO assign(@PathVariable("id") UUID id,
                                      @Valid @RequestBody IncidentAssignRequest req) {
        IncidentEntity e = incidents.assign(id, req.getTechnicianId());
        return IncidentResponseDTO.fromEntity(e);
    }

    /**
     * Marca una incidencia como resuelta (solo técnicos o admins).
     */
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN','TECH')")
    public IncidentResponseDTO resolve(@PathVariable("id") UUID id,
                                       @Valid @RequestBody IncidentResolveRequest req) {
        AppUser tech = currentUser.getOrThrow();
        IncidentEntity e = incidents.resolve(id, tech.getId(), req.getResolutionNotes());
        return IncidentResponseDTO.fromEntity(e);
    }

    /**
     * Consulta una incidencia concreta (técnicos/admins; el reporter puede verla vía /me).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TECH')")
    public IncidentResponseDTO get(@PathVariable("id") UUID id) {
        IncidentEntity e = incidents.getOrThrow(id);
        return IncidentResponseDTO.fromEntity(e);
    }
}
