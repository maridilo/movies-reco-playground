package com.acme.reco.domain.cf;

import com.acme.reco.api.dto.MovieWithScore;
import com.acme.reco.persistence.entity.MovieEntity;
import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import com.acme.reco.persistence.repo.RatingJpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollaborativeFilteringService {

    private final RatingJpaRepository ratingsRepo;
    private final MovieJpaRepository moviesRepo;

    public CollaborativeFilteringService(RatingJpaRepository ratingsRepo, MovieJpaRepository moviesRepo) {
        this.ratingsRepo = ratingsRepo;
        this.moviesRepo = moviesRepo;
    }

    public List<MovieWithScore> recommendFor(UUID userId, int topKNeighbors, int limit) {
        // 1) matriz user -> (movie->score)
        Map<UUID, Map<UUID, Integer>> R = loadMatrix();

        Map<UUID, Integer> target = R.getOrDefault(userId, Map.of());
        if (R.size() <= 1) return List.of(); // sin vecinos

        // 2) medias por usuario (para centrar)
        Map<UUID, Double> means = new HashMap<>();
        for (var e : R.entrySet()) {
            double m = e.getValue().values().stream().mapToInt(i -> i).average().orElse(0.0);
            means.put(e.getKey(), m);
        }

        // 3) similitud coseno entre usuarios (sobre items comunes, con media-centrado)
        Map<UUID, Double> sims = new HashMap<>();
        for (var u : R.keySet()) {
            if (u.equals(userId)) continue;
            double s = cosineCentered(target, R.get(u), means.get(userId), means.get(u));
            if (!Double.isNaN(s) && s > 0) sims.put(u, s);
        }

        // top-K vecinos
        List<Map.Entry<UUID, Double>> neighbors = sims.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(Math.max(1, topKNeighbors))
                .toList();

        if (neighbors.isEmpty()) return List.of();

        // 4) scoring para items no valorados por el target
        Set<UUID> already = target.keySet();
        // candidatos = cualquier movie valorada por algún vecino y no por target
        Set<UUID> candidates = new HashSet<>();
        for (var nb : neighbors) candidates.addAll(R.get(nb.getKey()).keySet());
        candidates.removeAll(already);

        List<MovieWithScore> scored = new ArrayList<>();
        for (UUID mId : candidates) {
            double num = 0.0, den = 0.0;
            for (var nb : neighbors) {
                UUID v = nb.getKey();
                Double sim = nb.getValue();
                Integer rv = R.get(v).get(mId);
                if (rv == null) continue;
                num += sim * (rv - means.get(v));
                den += Math.abs(sim);
            }
            if (den > 0) {
                double pred = means.get(userId) + (num / den); // predicción 1..5 aprox.
                MovieEntity m = moviesRepo.findById(mId).orElse(null);
                if (m != null) scored.add(new MovieWithScore(m, pred));
            }
        }

        // normalizamos a 0..1 para poder mezclar después si hace falta
        normalizeScores0to1(scored);

        return scored.stream()
                .sorted(Comparator.comparingDouble(MovieWithScore::score).reversed())
                .limit(Math.max(1, limit))
                .toList();
    }

    private Map<UUID, Map<UUID, Integer>> loadMatrix() {
        Map<UUID, Map<UUID, Integer>> R = new HashMap<>();
        List<RatingEntity> all = ratingsRepo.findAll();
        for (RatingEntity r : all) {
            UUID u = r.getId().getUserId();
            UUID m = r.getId().getMovieId();
            R.computeIfAbsent(u, k -> new HashMap<>()).put(m, r.getScore());
        }
        return R;
    }

    // coseno sobre valores centrados (r - media)
    private static double cosineCentered(Map<UUID, Integer> a, Map<UUID, Integer> b, double meanA, double meanB) {
        Set<UUID> common = new HashSet<>(a.keySet());
        common.retainAll(b.keySet());
        if (common.isEmpty()) return Double.NaN;

        double dot = 0, na = 0, nb = 0;
        for (UUID m : common) {
            double ax = a.get(m) - meanA;
            double bx = b.get(m) - meanB;
            dot += ax * bx;
            na += ax * ax;
            nb += bx * bx;
        }
        double denom = Math.sqrt(na) * Math.sqrt(nb);
        if (denom == 0) return Double.NaN;
        return dot / denom; // [-1,1]
    }

    private static void normalizeScores0to1(List<MovieWithScore> list) {
        if (list.isEmpty()) return;
        double min = list.stream().mapToDouble(MovieWithScore::score).min().orElse(0);
        double max = list.stream().mapToDouble(MovieWithScore::score).max().orElse(1);
        double span = (max - min);
        if (span <= 0) {
            // todo igual -> pon 0.5
            for (int i = 0; i < list.size(); i++) {
                MovieWithScore ms = list.get(i);
                list.set(i, new MovieWithScore(ms.movie(), 0.5));
            }
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MovieWithScore ms = list.get(i);
            double z = (ms.score() - min) / span;
            list.set(i, new MovieWithScore(ms.movie(), z));
        }
    }
}
