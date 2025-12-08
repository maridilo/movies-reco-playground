package com.acme.reco.persistence.spec;

import com.acme.reco.persistence.entity.MovieEntity;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecs {
    public static Specification<MovieEntity> titleContains(String q) {
        return (r, cq, cb) -> q == null ? null :
                cb.like(cb.lower(r.get("title")), "%" + q.toLowerCase() + "%");
    }
    public static Specification<MovieEntity> genreHas(String g) {
        return (r,cq,cb) -> g == null ? null :
                cb.like(cb.lower(r.get("genresCsv")), "%" + g.toLowerCase() + "%");
    }
    public static Specification<MovieEntity> yearBetween(Integer from, Integer to) {
        return (r,cq,cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(r.get("releaseYear"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(r.get("releaseYear"), from);
            return cb.lessThanOrEqualTo(r.get("releaseYear"), to);
        };
    }
}