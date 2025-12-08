package com.acme.reco.persistence.repo.projections;

import java.util.UUID;

public interface MovieAgg {
    UUID getMovieId();
    Double getAvg();   // promedio
    Long getCnt();     // nº valoraciones
}