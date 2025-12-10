package com.acme.reco.api.controller;

import com.acme.reco.api.service.BillingService;
import com.acme.reco.persistence.entity.InvoiceEntity;
import com.acme.reco.persistence.entity.SubscriptionEntity;
import com.acme.reco.persistence.repo.InvoiceRepository;
import com.acme.reco.persistence.repo.SubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.acme.reco.api.service.BillingService;
import com.acme.reco.api.dto.IncomeStatDTO;
import java.util.List;

import java.util.*;

@RestController
@RequestMapping("/api/admin/billing")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBillingController {

    private final SubscriptionRepository subs;
    private final InvoiceRepository invoices;
    private final BillingService billingService;


    public AdminBillingController(SubscriptionRepository subs, InvoiceRepository invoices, BillingService billingService) {
        this.subs = subs;
        this.invoices = invoices;
        this.billingService = billingService;
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionEntity> allSubs() {
        return subs.findAll();
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceEntity>> lastInvoices(@RequestParam(defaultValue = "50") int limit) {
        List<InvoiceEntity> all = invoices.findAll();
        all.sort(Comparator.comparing(InvoiceEntity::getCreatedAt).reversed());
        if (limit > 0 && all.size() > limit) {
            all = all.subList(0, limit);
        }
        return ResponseEntity.ok(all);
    }

    @GetMapping("/stats/income")
    @PreAuthorize("hasRole('ADMIN')")
    public List<IncomeStatDTO> incomeStats(
            @RequestParam(name = "months", defaultValue = "6") int months
    ) {
        return billingService.incomeLastMonths(months);
    }
}
