package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface MovieJpaRepository extends JpaRepository<MovieEntity, UUID>, JpaSpecificationExecutor<MovieEntity> {
    List<MovieEntity> findByTitleContainingIgnoreCase(String q);
}
