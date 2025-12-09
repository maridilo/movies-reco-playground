package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(name = "uk_sub_user", columnNames = "user_id")
)
public class SubscriptionEntity {

    @Id
    private UUID id;

    // FK a plans (el DDL añade constraint fk_sub_plan)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sub_plan"))
    private PlanEntity plan;

    // En el DDL NO había FK a users, así que guardamos el UUID directamente
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private SubscriptionStatus status; // ACTIVE, CANCELED, EXPIRED

    @Column(name = "started_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant startedAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant endsAt;

    @Column(name = "cancel_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant cancelAt;

    public SubscriptionEntity() {}

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PlanEntity getPlan() { return plan; }
    public void setPlan(PlanEntity plan) { this.plan = plan; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }

    public Instant getCancelAt() { return cancelAt; }
    public void setCancelAt(Instant cancelAt) { this.cancelAt = cancelAt; }
}
