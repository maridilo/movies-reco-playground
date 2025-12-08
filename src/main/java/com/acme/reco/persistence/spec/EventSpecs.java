package com.acme.reco.persistence.spec;

import com.acme.reco.persistence.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public class EventSpecs {
    public static Specification<EventEntity> typeIs(String t) {
        return (r, cq, cb) -> (t == null || t.isBlank()) ? null : cb.equal(r.get("type"), t);
    }
    public static Specification<EventEntity> adminIs(UUID id) {
        return (r, cq, cb) -> (id == null) ? null : cb.equal(r.get("adminId"), id);
    }
    public static Specification<EventEntity> movieIs(UUID id) {
        return (r, cq, cb) -> (id == null) ? null : cb.equal(r.get("movieId"), id);
    }
    public static Specification<EventEntity> between(Instant from, Instant to) {
        return (r, cq, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(r.get("ts"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(r.get("ts"), from);
            return cb.lessThanOrEqualTo(r.get("ts"), to);
        };
    }
}