package com.acme.reco.domain.service;

import com.acme.reco.persistence.entity.IncidentEntity;
import com.acme.reco.persistence.entity.IncidentStatus;
import com.acme.reco.persistence.repo.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class IncidentService {

    private final IncidentRepository incidents;

    public IncidentService(IncidentRepository incidents) {
        this.incidents = incidents;
    }

    @Transactional
    public IncidentEntity create(UUID reporterId, String title, String description) {
        IncidentEntity e = new IncidentEntity();
        e.setReporterId(reporterId);
        e.setTitle(title);
        e.setDescription(description);
        e.setStatus(IncidentStatus.OPEN);
        return incidents.save(e);
    }

    @Transactional(readOnly = true)
    public Page<IncidentEntity> search(Optional<IncidentStatus> status,
                                       Optional<UUID> technicianId,
                                       Optional<UUID> reporterId,
                                       Pageable pageable) {

        if (reporterId.isPresent()) {
            return incidents.findAllByReporterId(reporterId.get(), pageable);
        }
        if (status.isPresent() && technicianId.isPresent()) {
            return incidents.findAllByStatusAndTechnicianId(status.get(), technicianId.get(), pageable);
        }
        if (status.isPresent()) {
            return incidents.findAllByStatus(status.get(), pageable);
        }
        if (technicianId.isPresent()) {
            return incidents.findAllByTechnicianId(technicianId.get(), pageable);
        }
        return incidents.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public IncidentEntity getOrThrow(UUID id) {
        return incidents.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada: " + id));
    }

    @Transactional
    public IncidentEntity assign(UUID id, UUID technicianId) {
        IncidentEntity e = getOrThrow(id);
        e.setTechnicianId(technicianId);
        if (e.getStatus() == IncidentStatus.OPEN) {
            e.setStatus(IncidentStatus.IN_PROGRESS);
        }
        return incidents.save(e);
    }

    @Transactional
    public IncidentEntity resolve(UUID id, UUID technicianId, String resolutionNotes) {
        IncidentEntity e = getOrThrow(id);
        e.setTechnicianId(technicianId);
        e.setStatus(IncidentStatus.RESOLVED);
        e.setResolutionNotes(resolutionNotes);
        e.setResolvedAt(Instant.now());
        return incidents.save(e);
    }
}
