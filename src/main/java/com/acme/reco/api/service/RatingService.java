// com/acme/reco/api/service/RatingService.java
package com.acme.reco.api.service;

import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RatingService {
    private final RatingJpaRepository ratings;

    public RatingService(RatingJpaRepository ratings) {
        this.ratings = ratings;
    }

    public MovieRatingAggregate aggregateForMovie(UUID movieId) {
        Double avg = ratings.avgByMovie(movieId);
        Long cnt   = ratings.countByMovie(movieId);
        return new MovieRatingAggregate(avg == null ? 0.0 : avg, cnt == null ? 0L : cnt);
    }

    @Transactional
    public void saveAndRecalc(RatingEntity r) {
        ratings.save(r);
    }

    public record MovieRatingAggregate(double average, long count) {}
}
