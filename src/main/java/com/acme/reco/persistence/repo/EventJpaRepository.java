package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.*;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID>, JpaSpecificationExecutor<EventEntity> { }
