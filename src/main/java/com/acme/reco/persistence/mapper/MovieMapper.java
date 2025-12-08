package com.acme.reco.persistence.mapper;

import com.acme.reco.domain.model.Movie;
import com.acme.reco.persistence.entity.MovieEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class MovieMapper {

    private MovieMapper(){}

    public static Movie toDomain(MovieEntity e) {
        return new Movie(
                e.getId(),
                e.getTitle(),
                e.getReleaseYear(),
                splitCsv(e.getGenresCsv()),
                splitCsv(e.getTagsCsv()),
                e.getOverview()
        );
    }

    public static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static String joinCsv(List<String> list) {
        return list == null || list.isEmpty() ? null : String.join(",", list);
    }

    public static MovieEntity from(UUID id, String title, Integer year, String overview, List<String> genres, List<String> tags) {
        var m = new MovieEntity();
        m.setId(id);
        m.setTitle(title);
        m.setReleaseYear(year);
        m.setOverview(overview);
        m.setGenresCsv(joinCsv(genres));
        m.setTagsCsv(joinCsv(tags));
        return m;
    }
}
