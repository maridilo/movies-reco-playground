// src/main/java/com/acme/reco/persistence/entity/PlanEntity.java
package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "plans", uniqueConstraints = {
        @UniqueConstraint(name = "uk_plans_code", columnNames = "code")
})
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)   // <-- genera UUID automáticamente
    private UUID id;

    @Column(nullable = false, length = 40, unique = true)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    // Evita la palabra reservada "interval" en H2
    @Enumerated(EnumType.STRING)
    @Column(name = "interval_code", nullable = false, length = 20)
    private BillingInterval interval;   // enum con valores p.ej. MONTH

    @Column(name = "price_cents", nullable = false)
    private int priceCents;

    @Column(nullable = false, length = 10)
    private String currency = "EUR";

    @Column(nullable = false)
    private boolean active = true;

    public PlanEntity() {}

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public BillingInterval getInterval() { return interval; }
    public void setInterval(BillingInterval interval) { this.interval = interval; }
}
