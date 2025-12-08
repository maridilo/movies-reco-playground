package com.acme.reco.recommender;

import com.acme.reco.domain.model.*;
import com.acme.reco.domain.ports.Recommender;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.util.*;
import java.util.stream.IntStream;

@Service
@Profile("dummy")
public class DummyRecommender implements Recommender {

    @Override
    public List<Recommendation> topN(UUID userId, int k, Optional<com.acme.reco.domain.model.Filter> filter) {
        return java.util.stream.IntStream.range(0, k)
                .mapToObj(i -> new Recommendation(
                        UUID.randomUUID(),
                        1.0 - i * 0.05,
                        java.util.List.of("demo: similar a tu perfil", "tag: prueba")
                ))
                .toList();
    }


    @Override
    public List<Recommendation> similarItems(UUID itemId, int k) {
        return IntStream.range(0, k)
                .mapToObj(i -> new Recommendation(
                        UUID.randomUUID(),
                        1.0 - i * 0.04,
                        List.of("demo: similar a " + itemId)
                ))
                .toList();
    }

    @Override
    public Explanation explain(UUID userId, UUID itemId) {
        return new Explanation("Recomendado (DEMO) por similitud genérica.");
    }
}
