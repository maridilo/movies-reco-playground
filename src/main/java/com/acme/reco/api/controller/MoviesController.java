package com.acme.reco.api.controller;

import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.spec.MovieSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MoviesController {

    private final MovieJpaRepository movies;

    public MoviesController(MovieJpaRepository movies) {
        this.movies = movies;
    }

    @GetMapping
    public Page<MovieEntity> list(
            Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear
    ) {
        Specification<MovieEntity> spec = Specification
                .where(MovieSpecs.titleContains(q))
                .and(MovieSpecs.genreHas(genre))
                .and(MovieSpecs.yearBetween(fromYear, toYear));

        return movies.findAll(spec, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieEntity> byId(@PathVariable UUID id) {
        return movies.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
