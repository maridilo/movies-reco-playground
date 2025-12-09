package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<PlanEntity, UUID> {

    Optional<PlanEntity> findByCode(String code);
    List<PlanEntity> findAllByActiveTrue();
}
