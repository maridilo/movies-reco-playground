package com.acme.reco.api.controller;

import com.acme.reco.api.service.BillingService;
import com.acme.reco.persistence.entity.InvoiceEntity;
import com.acme.reco.persistence.entity.PlanEntity;
import com.acme.reco.persistence.entity.SubscriptionEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billing;

    public BillingController(BillingService billing) {
        this.billing = billing;
    }

    @GetMapping("/plans")
    public List<PlanEntity> activePlans() {
        return billing.activePlans();
    }

    @GetMapping("/subscription/{userId}")
    public ResponseEntity<SubscriptionEntity> getSubscription(@PathVariable UUID userId) {
        return billing.findSubscription(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<SubscriptionEntity> subscribe(
            @PathVariable UUID userId,
            @RequestParam @NotBlank String planCode
    ) {
        return ResponseEntity.ok(billing.subscribe(userId, planCode));
    }

    @PostMapping("/cancel/{userId}")
    public ResponseEntity<Void> cancel(@PathVariable UUID userId) {
        billing.cancel(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invoices/{userId}")
    public List<InvoiceEntity> invoices(@PathVariable UUID userId) {
        return billing.userInvoices(userId);
    }
}
