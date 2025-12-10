package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.IncidentEntity;
import com.acme.reco.persistence.entity.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<IncidentEntity, UUID> {

    Page<IncidentEntity> findAllByReporterId(UUID reporterId, Pageable pageable);

    Page<IncidentEntity> findAllByTechnicianId(UUID technicianId, Pageable pageable);

    Page<IncidentEntity> findAllByStatus(IncidentStatus status, Pageable pageable);

    Page<IncidentEntity> findAllByStatusAndTechnicianId(IncidentStatus status, UUID technicianId, Pageable pageable);
}
