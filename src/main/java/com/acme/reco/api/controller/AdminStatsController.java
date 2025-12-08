// com/acme/reco/api/controller/AdminStatsController.java
package com.acme.reco.api.controller;

import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final RatingJpaRepository ratings;

    public AdminStatsController(RatingJpaRepository ratings) {
        this.ratings = ratings;
    }

    @GetMapping("/top-movies/by-count")
    public ResponseEntity<List<RatingJpaRepository.MovieAgg>> topMoviesByCount(
            @RequestParam(defaultValue = "10") int k) {
        return ResponseEntity.ok(ratings.topMoviesByCount(PageRequest.of(0, Math.max(1, k))));
    }

    @GetMapping("/top-movies/by-avg")
    public ResponseEntity<List<RatingJpaRepository.MovieAgg>> topMoviesByAvg(
            @RequestParam(defaultValue = "5") long min,
            @RequestParam(defaultValue = "10") int k) {
        return ResponseEntity.ok(ratings.topMoviesByAvg(min, PageRequest.of(0, Math.max(1, k))));
    }

    @GetMapping("/top-raters")
    public ResponseEntity<List<RatingJpaRepository.UserAgg>> topRaters(
            @RequestParam(defaultValue = "10") int k) {
        return ResponseEntity.ok(ratings.topRaters(PageRequest.of(0, Math.max(1, k))));
    }
}
