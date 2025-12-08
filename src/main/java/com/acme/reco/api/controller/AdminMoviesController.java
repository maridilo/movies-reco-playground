package com.acme.reco.api.controller;

import com.acme.reco.api.service.AuditService;
import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/movies")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMoviesController {

    private final MovieJpaRepository movies;
    private final AuditService audit;

    public AdminMoviesController(MovieJpaRepository movies, AuditService audit) {
        this.movies = movies; this.audit = audit;
    }

    public record UpsertMovie(
            @NotBlank String title,
            String overview,
            Integer releaseYear,
            String genresCsv,
            String tagsCsv
    ) {}

    @PostMapping
    public ResponseEntity<MovieEntity> create(@Valid @RequestBody UpsertMovie body) {
        var m = new MovieEntity();
        m.setId(UUID.randomUUID());
        m.setTitle(body.title());
        m.setOverview(body.overview());
        m.setReleaseYear(body.releaseYear());
        m.setGenresCsv(body.genresCsv());
        m.setTagsCsv(body.tagsCsv());
        var saved = movies.save(m);
        audit.record("ADD", saved.getId(), "Alta de película: " + saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieEntity> update(@PathVariable UUID id, @Valid @RequestBody UpsertMovie body) {
        var m = movies.findById(id).orElse(null);
        if (m == null) return ResponseEntity.notFound().build();
        m.setTitle(body.title());
        m.setOverview(body.overview());
        m.setReleaseYear(body.releaseYear());
        m.setGenresCsv(body.genresCsv());
        m.setTagsCsv(body.tagsCsv());
        var saved = movies.save(m);
        audit.record("UPDATE", saved.getId(), "Modificación de película: " + saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        var m = movies.findById(id).orElse(null);
        if (m == null) return ResponseEntity.notFound().build();
        movies.deleteById(id);
        audit.record("DELETE", id, "Baja de película: " + m.getTitle());
        return ResponseEntity.noContent().build();
    }
}