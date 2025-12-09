package com.acme.reco.api.service;

import com.acme.reco.persistence.entity.InvoiceEntity;
import com.acme.reco.persistence.entity.PlanEntity;
import com.acme.reco.persistence.entity.SubscriptionEntity;
import com.acme.reco.persistence.entity.SubscriptionStatus;
import com.acme.reco.persistence.repo.InvoiceRepository;
import com.acme.reco.persistence.repo.PlanRepository;
import com.acme.reco.persistence.repo.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BillingService {

    // NOTA: nombres EXACTOS como pediste
    private final PlanRepository planrepository;
    private final SubscriptionRepository subscriptionrepository;
    private final InvoiceRepository invoicerepository;

    public BillingService(PlanRepository planrepository,
                          SubscriptionRepository subscriptionrepository,
                          InvoiceRepository invoicerepository) {
        this.planrepository = planrepository;
        this.subscriptionrepository = subscriptionrepository;
        this.invoicerepository = invoicerepository;
    }

    @Transactional(readOnly = true)
    public List<PlanEntity> activePlans() {
        return planrepository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<SubscriptionEntity> findSubscription(UUID userId) {
        return subscriptionrepository.findByUserId(userId);
    }

    public SubscriptionEntity subscribe(UUID userId, String planCode) {
        PlanEntity plan = planrepository.findByCode(planCode)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado: " + planCode));

        Instant now = Instant.now();
        Instant endsAt = now.plus(30, ChronoUnit.DAYS);

        SubscriptionEntity sub = subscriptionrepository.findByUserId(userId).orElseGet(() -> {
            SubscriptionEntity s = new SubscriptionEntity();
            s.setId(UUID.randomUUID());
            s.setUserId(userId);
            return s;
        });

        sub.setPlan(plan);
        sub.setStartedAt(now);
        sub.setEndsAt(endsAt);
        sub.setCancelAt(null);
        sub.setStatus(SubscriptionStatus.ACTIVE);

        sub = subscriptionrepository.save(sub);

        // Genera factura mock “PAID” (status es String en InvoiceEntity)
        InvoiceEntity inv = new InvoiceEntity();
        inv.setId(UUID.randomUUID());
        inv.setUserId(userId);
        inv.setPlanCode(plan.getCode());
        inv.setAmountCents(plan.getPriceCents());
        inv.setCurrency(plan.getCurrency());
        inv.setCreatedAt(now);
        inv.setPeriodStart(now);
        inv.setPeriodEnd(endsAt);
        inv.setStatus("PAID");
        invoicerepository.save(inv);

        return sub;
    }

    public void cancel(UUID userId) {
        SubscriptionEntity sub = subscriptionrepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No hay suscripción activa"));
        sub.setCancelAt(sub.getEndsAt());
        sub.setStatus(SubscriptionStatus.CANCELED);
        subscriptionrepository.save(sub);
    }

    @Transactional(readOnly = true)
    public List<InvoiceEntity> userInvoices(UUID userId) {
        return invoicerepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
