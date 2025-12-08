package com.acme.reco.data;

import com.acme.reco.domain.ports.PopularityProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Popularidad simple: cuenta ponderada por score (1..5), normalizada por el máximo. */
@Component
public class InMemoryPopularityProvider implements PopularityProvider {

    private final Map<UUID, Double> counts = new ConcurrentHashMap<>();

    public InMemoryPopularityProvider() {
        // Semillas opcionales para mejorar cold-start (elige algunas)
        // Si no quieres semillas, comenta estas líneas.
        counts.put(InMemoryMovieCatalog.MATRIX, 50.0);
        counts.put(InMemoryMovieCatalog.INCEPTION, 45.0);
        counts.put(InMemoryMovieCatalog.INTERSTELLAR, 40.0);
        counts.put(InMemoryMovieCatalog.TOY_STORY, 35.0);
        counts.put(InMemoryMovieCatalog.GODFATHER, 30.0);
    }

    @Override
    public double score(UUID movieId) {
        double max = counts.values().stream().mapToDouble(d -> d).max().orElse(1.0);
        double c = counts.getOrDefault(movieId, 0.0);
        return max == 0 ? 0.0 : c / max; // 0..1
    }

    @Override
    public void bump(UUID movieId, int score) {
        // Pondera por score (likes pesan más)
        counts.merge(movieId, (double) Math.max(1, score), Double::sum);
    }
}
