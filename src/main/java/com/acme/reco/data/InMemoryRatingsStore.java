package com.acme.reco.data;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryRatingsStore {

    // ratings[userId][movieId] = score (1-5)
    private final Map<UUID, Map<UUID, Integer>> ratings = new HashMap<>();

    public void upsert(UUID userId, UUID movieId, int score) {
        ratings.computeIfAbsent(userId, k -> new HashMap<>()).put(movieId, score);
    }

    public Map<UUID, Integer> byUser(UUID userId) {
        return ratings.getOrDefault(userId, Map.of());
    }

    /** Devuelve las pelis "liked" por encima del umbral (ej. 4). */
    public List<UUID> likedMovies(UUID userId, int threshold) {
        var map = ratings.getOrDefault(userId, Map.of());
        List<UUID> liked = new ArrayList<>();
        for (var e : map.entrySet()) {
            if (e.getValue() >= threshold) liked.add(e.getKey());
        }
        return liked;
    }
}
