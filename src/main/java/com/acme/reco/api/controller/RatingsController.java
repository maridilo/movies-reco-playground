package com.acme.reco.api.controller;

import com.acme.reco.api.dto.MovieRatingRequest;
import com.acme.reco.api.service.RatingService;
import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.entity.RatingId;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingsController {

    private final RatingJpaRepository ratingsRepo;
    private final RatingService ratingService;

    public RatingsController(RatingJpaRepository ratingsRepo, RatingService ratingService) {
        this.ratingsRepo = ratingsRepo;
        this.ratingService = ratingService;
    }

    /**
     * Crea una valoración (conflict si ya existe).
     * Importante: invalida caches de recomendaciones/similares/estadísticas.
     */
    @PostMapping("/by-movie/{movieId}")
    @CacheEvict(cacheNames = {"recs", "similar", "stats"}, allEntries = true)
    public ResponseEntity<Void> create(
            @PathVariable UUID movieId,
            @Valid @RequestBody MovieRatingRequest body
    ) {
        if (ratingsRepo.existsByIdMovieIdAndIdUserId(movieId, body.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Rating already exists");
        }
        var r = new RatingEntity();
        r.setId(new RatingId(movieId, body.userId()));
        r.setScore(body.score());
        r.setComment(body.comment());
        r.setTs(Instant.now());

        ratingService.saveAndRecalc(r); // guarda y recalcula métricas derivadas
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/by-movie/{movieId}")
    public List<RatingEntity> byMovie(@PathVariable UUID movieId) {
        return ratingsRepo.findAllByIdMovieId(movieId);
    }

    @GetMapping("/by-user/{userId}")
    public List<RatingEntity> byUser(@PathVariable UUID userId) {
        return ratingsRepo.findAllByIdUserId(userId);
    }
}
