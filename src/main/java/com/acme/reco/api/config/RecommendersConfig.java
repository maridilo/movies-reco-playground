// src/main/java/com/acme/reco/api/config/RecommendersConfig.java
package com.acme.reco.api.config;

import com.acme.reco.api.dto.MovieWithScore;
import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Configuration
public class RecommendersConfig {

    @Bean("recommender")
    public BiFunction<UUID, Integer, List<MovieWithScore>> recommender(
            MovieJpaRepository moviesRepo, RatingJpaRepository ratingsRepo) {

        return (userId, limit) -> {
            var liked = ratingsRepo.findAllByIdUserIdAndScoreGreaterThanEqual(userId, 4);
            if (liked.isEmpty()) return List.of();

            Set<UUID> alreadyRated = liked.stream().map(r -> r.getId().getMovieId()).collect(Collectors.toSet());
            Set<String> userGenres = new HashSet<>();
            Set<String> userTags   = new HashSet<>();

            liked.forEach(r -> moviesRepo.findById(r.getId().getMovieId()).ifPresent(m -> {
                userGenres.addAll(toSet(m.getGenresCsv()));
                userTags.addAll(toSet(m.getTagsCsv()));
            }));

            int max = (limit != null && limit > 0) ? limit : 20;

            return moviesRepo.findAll().stream()
                    .filter(m -> !alreadyRated.contains(m.getId()))
                    .map(m -> {
                        double score = (jaccard(userGenres, toSet(m.getGenresCsv()))
                                + jaccard(userTags,   toSet(m.getTagsCsv()))) / 2.0;
                        return new MovieWithScore(m, score);
                    })
                    .filter(ms -> ms.score() > 0.0)
                    .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                    .limit(max)
                    .collect(Collectors.toList());
        };
    }

    @Bean("similar")
    public BiFunction<UUID, Integer, List<MovieWithScore>> similar(MovieJpaRepository moviesRepo) {
        return (movieId, limit) -> {
            var base = moviesRepo.findById(movieId).orElse(null);
            if (base == null) return List.of();

            Set<String> baseGenres = toSet(base.getGenresCsv());
            Set<String> baseTags   = toSet(base.getTagsCsv());
            int max = (limit != null && limit > 0) ? limit : 20;

            return moviesRepo.findAll().stream()
                    .filter(m -> !m.getId().equals(movieId))
                    .map(m -> {
                        double score = (jaccard(baseGenres, toSet(m.getGenresCsv()))
                                + jaccard(baseTags,   toSet(m.getTagsCsv()))) / 2.0;
                        return new MovieWithScore(m, score);
                    })
                    .filter(ms -> ms.score() > 0.0)
                    .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                    .limit(max)
                    .collect(Collectors.toList());
        };
    }

    // helpers
    private static Set<String> toSet(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptySet();
        return Arrays.stream(csv.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(String::toLowerCase).collect(Collectors.toSet());
    }

    private static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a); inter.retainAll(b);
        Set<String> union = new HashSet<>(a); union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) inter.size() / (double) union.size();
    }
}
