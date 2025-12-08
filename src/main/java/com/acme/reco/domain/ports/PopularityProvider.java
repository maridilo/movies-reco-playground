package com.acme.reco.domain.ports;

import java.util.UUID;

public interface PopularityProvider {
    double score(UUID movieId);        // normalizado 0..1
    void bump(UUID movieId, int score); // incrementa con cada rating
}
