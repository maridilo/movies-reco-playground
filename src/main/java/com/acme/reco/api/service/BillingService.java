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
import com.acme.reco.api.dto.IncomeStatDTO;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public List<IncomeStatDTO> incomeLastMonths(int months) {
        if (months <= 0) {
            months = 6;
        }

        // Fecha mínima (inicio del mes N meses atrás)
        YearMonth nowYm = YearMonth.now(ZoneOffset.UTC);
        YearMonth minYm = nowYm.minusMonths(months - 1);
        LocalDate minDate = minYm.atDay(1);
        Instant minInstant = minDate.atStartOfDay().toInstant(ZoneOffset.UTC);

        // Cargamos todas las facturas desde esa fecha
        List<InvoiceEntity> invoices = invoicerepository.findAll().stream()
                .filter(inv -> inv.getCreatedAt() != null && !inv.getCreatedAt().isBefore(minInstant))
                .toList();

        // Agrupamos por YearMonth y sumamos amountCents
        Map<YearMonth, Long> totals = invoices.stream()
                .collect(Collectors.groupingBy(
                        inv -> {
                            LocalDate d = inv.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
                            return YearMonth.of(d.getYear(), d.getMonth());
                        },
                        Collectors.summingLong(inv -> inv.getAmountCents()
                ))
                );

        // Construimos lista ordenada cronológicamente
        return totals.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new IncomeStatDTO(
                        e.getKey().getYear(),
                        e.getKey().getMonthValue(),
                        e.getValue()
                ))
                .toList();
    }
}
