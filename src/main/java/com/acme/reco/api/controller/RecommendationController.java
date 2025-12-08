package com.acme.reco.api.controller;

import com.acme.reco.api.dto.MovieRatingRequest;
import com.acme.reco.api.dto.MovieWithScore;
import com.acme.reco.domain.cf.CollaborativeFilteringService;
import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.entity.RatingId;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class RecommendationController {

    private final MovieJpaRepository moviesRepo;
    private final RatingJpaRepository ratingsRepo;
    private final CollaborativeFilteringService cfService;

    public RecommendationController(MovieJpaRepository moviesRepo,
                                    RatingJpaRepository ratingsRepo,
                                    CollaborativeFilteringService cfService) {
        this.moviesRepo = moviesRepo;
        this.ratingsRepo = ratingsRepo;
        this.cfService = cfService;
    }

    @PostMapping("/movies/{id}/ratings")
    public ResponseEntity<Void> rateMovie(@PathVariable("id") UUID movieId,
                                          @Valid @RequestBody MovieRatingRequest body) {
        if (ratingsRepo.existsByIdMovieIdAndIdUserId(movieId, body.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Rating already exists");
        }
        RatingEntity e = new RatingEntity();
        e.setId(new RatingId(movieId, body.userId()));
        e.setScore(body.score());
        e.setComment(body.comment());
        e.setTs(Instant.now());
        ratingsRepo.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Cacheable(cacheNames = "similar", key = "#movieId + '-' + 20") // si aceptas 'limit', inclúyelo en la key
    @GetMapping("/movies/{id}/similar")
    public ResponseEntity<List<MovieWithScore>> similar(@PathVariable("id") UUID movieId) {
        MovieEntity base = moviesRepo.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        Set<String> baseTokens = tokens(base);

        List<MovieWithScore> scored = moviesRepo.findAll().stream()
                .filter(m -> !m.getId().equals(movieId))
                .map(m -> new MovieWithScore(m, jaccard(baseTokens, tokens(m))))
                .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                .limit(20)
                .toList();

        return ResponseEntity.ok(scored);
    }

    @Cacheable(cacheNames = "recs", key = "#userId + '-' + 20")
    @GetMapping("/users/{id}/recommendations")
    public ResponseEntity<List<MovieWithScore>> recommendations(@PathVariable("id") UUID userId) {
        List<RatingEntity> liked = ratingsRepo.findAllByIdUserIdAndScoreGreaterThanEqual(userId, 4);
        if (liked.isEmpty()) {
            // fallback sencillo
            List<MovieWithScore> fallback = moviesRepo.findAll().stream()
                    .limit(50)
                    .map(m -> new MovieWithScore(m, 0.1))
                    .toList();
            return ResponseEntity.ok(fallback);
        }

        Set<UUID> alreadyRated = liked.stream()
                .map(r -> r.getId().getMovieId())
                .collect(Collectors.toSet());

        // Fuerza el tipo a MovieEntity para evitar que el compilador vea Object
        Set<String> userProfile = liked.stream()
                .map(r -> moviesRepo.findById(r.getId().getMovieId()).orElse(null))
                .filter(Objects::nonNull)
                .map((MovieEntity m) -> tokens(m))   // <--- aquí fijamos el tipo
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        List<MovieWithScore> scored = moviesRepo.findAll().stream()
                .filter(m -> !alreadyRated.contains(m.getId()))
                .map(m -> new MovieWithScore(m, jaccard(userProfile, tokens(m))))
                .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                .limit(20)
                .toList();

        return ResponseEntity.ok(scored);
    }

    @GetMapping("/users/{id}/recommendations/cf")
    public ResponseEntity<List<MovieWithScore>> recommendationsCf(
            @PathVariable("id") UUID userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "30") int neighbors
    ) {
        var cf = cfService.recommendFor(userId, neighbors, limit);
        return ResponseEntity.ok(cf);
    }

    @GetMapping("/users/{id}/recommendations/hybrid")
    public ResponseEntity<List<MovieWithScore>> recommendationsHybrid(
            @PathVariable("id") UUID userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "30") int neighbors,
            @RequestParam(defaultValue = "0.6") double alpha
    ) {
        alpha = Math.max(0.0, Math.min(1.0, alpha));

        // --- CB base ---
        List<RatingEntity> liked = ratingsRepo.findAllByIdUserIdAndScoreGreaterThanEqual(userId, 4);
        Set<UUID> already = liked.stream().map(r -> r.getId().getMovieId()).collect(Collectors.toSet());

        Set<String> profile = liked.stream()
                .map(r -> moviesRepo.findById(r.getId().getMovieId()).orElse(null))
                .filter(Objects::nonNull)
                .map((MovieEntity m) -> tokens(m))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        List<MovieWithScore> cb = moviesRepo.findAll().stream()
                .filter(m -> !already.contains(m.getId()))
                .map(m -> new MovieWithScore(m, jaccard(profile, tokens(m)))) // 0..1
                .toList();

        // --- CF normalizado 0..1 ---
        var cf = cfService.recommendFor(userId, neighbors, limit * 2);
        Map<UUID, Double> cfIndex = cf.stream()
                .collect(Collectors.toMap(ms -> ms.movie().getId(), MovieWithScore::score, (a,b)->a));

        // --- Mezcla ---
        double finalAlpha = alpha;
        List<MovieWithScore> hybrid = cb.stream()
                .map(ms -> {
                    double cbScore = ms.score();
                    double cfScore = cfIndex.getOrDefault(ms.movie().getId(), 0.0);
                    double blended = finalAlpha * cbScore + (1 - finalAlpha) * cfScore;
                    return new MovieWithScore(ms.movie(), blended);
                })
                .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                .limit(limit)
                .toList();

        return ResponseEntity.ok(hybrid);
    }

    private static Set<String> tokens(MovieEntity m) {
        Set<String> s = new HashSet<>();
        if (m.getGenresCsv() != null) {
            for (String g : m.getGenresCsv().split(",")) {
                String t = g.trim().toLowerCase();
                if (!t.isEmpty()) s.add("g:" + t);
            }
        }
        if (m.getTagsCsv() != null) {
            for (String g : m.getTagsCsv().split(",")) {
                String t = g.trim().toLowerCase();
                if (!t.isEmpty()) s.add("t:" + t);
            }
        }
        return s;
    }

    private static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> uni = new HashSet<>(a);
        uni.addAll(b);
        return uni.isEmpty() ? 0.0 : (double) inter.size() / (double) uni.size();
    }
}
