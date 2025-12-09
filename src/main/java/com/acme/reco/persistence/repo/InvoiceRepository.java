package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {
    List<InvoiceEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
