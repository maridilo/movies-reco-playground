package com.acme.reco.api.service;

import com.acme.reco.api.security.CurrentUser;
import com.acme.reco.persistence.entity.EventEntity;
import com.acme.reco.persistence.repo.EventJpaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {

    private final EventJpaRepository events;
    private final CurrentUser current;

    public AuditService(EventJpaRepository events, CurrentUser current) {
        this.events = events; this.current = current;
    }

    public void record(String type, UUID movieId, String detail) {
        var adminId = current.id().orElse(null);
        if (adminId == null) return; // por si llamas desde contexto no autenticado
        var e = new EventEntity();
        e.setId(UUID.randomUUID());
        e.setAdminId(adminId);
        e.setMovieId(movieId);
        e.setType(type);
        e.setTs(Instant.now());
        e.setDetail(detail);
        events.save(e);
    }
}