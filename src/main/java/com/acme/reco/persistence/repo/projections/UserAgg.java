package com.acme.reco.persistence.repo.projections;

import java.util.UUID;

public interface UserAgg {
    UUID getUserId();
    Long getCnt();     // nº valoraciones hechas por el usuario
}