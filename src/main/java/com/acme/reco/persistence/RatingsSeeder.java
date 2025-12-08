package com.acme.reco.persistence;

import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.entity.RatingId;
import com.acme.reco.persistence.repo.AppUserRepository;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("cb")
@Order(30)
public class RatingsSeeder implements CommandLineRunner {

    private final RatingJpaRepository ratings;
    private final AppUserRepository users;
    private final MovieJpaRepository movies;

    public RatingsSeeder(RatingJpaRepository ratings, AppUserRepository users, MovieJpaRepository movies) {
        this.ratings = ratings; this.users = users; this.movies = movies;
    }

    @Override public void run(String... args) {
        var user = users.findByEmail("user@local").orElse(null);
        if (user == null) return;

        // IDs fijos del DataInitializer (Matrix / Inception / Toy Story)
        var MATRIX = UUID.fromString("00000000-0000-0000-0000-000000000101");
        var INCEPTION = UUID.fromString("00000000-0000-0000-0000-000000000102");
        var TOY_STORY = UUID.fromString("00000000-0000-0000-0000-000000000104");

        upsert(user.getId(), MATRIX, 5, "clásico sci-fi");
        upsert(user.getId(), INCEPTION, 4, "nolan <3");
        upsert(user.getId(), TOY_STORY, 5, "pixar top");
    }

    private void upsert(UUID userId, UUID movieId, int score, String comment) {
        if (ratings.existsByIdMovieIdAndIdUserId(movieId, userId)) return;
        var r = new RatingEntity();
        r.setId(new RatingId(movieId, userId));
        r.setScore(score);
        r.setComment(comment);
        r.setTs(Instant.now());
        ratings.save(r);
    }
}
