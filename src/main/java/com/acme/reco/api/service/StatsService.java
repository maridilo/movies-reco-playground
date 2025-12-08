package com.acme.reco.api.service;

import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StatsService {

    private final RatingJpaRepository ratings;
    private final MovieJpaRepository movies;

    public StatsService(RatingJpaRepository ratings, MovieJpaRepository movies) {
        this.ratings = ratings;
        this.movies  = movies;
    }

    public List<TopMovieStat> topByAvg(int k) {
        var list = ratings.topByAvg(PageRequest.of(0, Math.max(1, k)));
        return map(list);
    }

    public List<TopMovieStat> topByCount(int k) {
        var list = ratings.topByCount(PageRequest.of(0, Math.max(1, k)));
        return map(list);
    }

    private List<TopMovieStat> map(List<RatingJpaRepository.MovieAgg> aggs) {
        var ids = aggs.stream().map(RatingJpaRepository.MovieAgg::getMovieId).toList();

        var titleById = StreamSupport.stream(movies.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(MovieEntity::getId, MovieEntity::getTitle));

        return aggs.stream()
                .map(a -> new TopMovieStat(
                        a.getMovieId(),
                        titleById.getOrDefault(a.getMovieId(), "(unknown)"),
                        Optional.ofNullable(a.getAvg()).orElse(0.0),
                        Optional.ofNullable(a.getCnt()).orElse(0L)
                ))
                .toList();
    }

    public record TopMovieStat(UUID movieId, String title, double avg, long cnt) {}
}
