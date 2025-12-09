package com.acme.reco.api.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FakePaymentGateway implements PaymentGateway {
    @Override
    public String charge(int amountCents, String currency, String description, UUID userId) {
        // Simula éxito siempre
        return "fake_tx_" + UUID.randomUUID();
    }
}
