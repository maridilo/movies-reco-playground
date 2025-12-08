package com.acme.reco.domain;

import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

public class SimilarService {
    private final MovieJpaRepository movies;

    public SimilarService(MovieJpaRepository movies) { this.movies = movies; }

    @Cacheable(cacheNames = "similar", key = "#movieId")
    public List<MovieEntity> similarTo(UUID movieId) {
        var target = movies.findById(movieId).orElseThrow();
        var base = toSet(target.getGenresCsv(), target.getTagsCsv());
        return movies.findAll().stream()
                .filter(m -> !m.getId().equals(movieId))
                .sorted(Comparator.comparingDouble((MovieEntity m) -> -jaccard(base, toSet(m.getGenresCsv(), m.getTagsCsv()))))
                .limit(10).toList();
    }

    private static Set<String> toSet(String... csvs) {
        return Arrays.stream(csvs)
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim).filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        var inter = new HashSet<>(a); inter.retainAll(b);
        var union = new HashSet<>(a); union.addAll(b);
        return (double) inter.size() / (double) union.size();
    }
}
