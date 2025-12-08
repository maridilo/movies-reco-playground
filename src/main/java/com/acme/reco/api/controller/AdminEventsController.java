package com.acme.reco.api.controller;

import com.acme.reco.persistence.entity.EventEntity;
import com.acme.reco.persistence.repo.EventJpaRepository;
import com.acme.reco.persistence.spec.EventSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventsController {

    private final EventJpaRepository events;

    public AdminEventsController(EventJpaRepository events) { this.events = events; }

    @GetMapping
    public Page<EventEntity> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID adminId,
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            Pageable pageable
    ) {
        var spec = EventSpecs.typeIs(type)
                .and(EventSpecs.adminIs(adminId))
                .and(EventSpecs.movieIs(movieId))
                .and(EventSpecs.between(from, to));
        return events.findAll(spec, pageable);
    }
}