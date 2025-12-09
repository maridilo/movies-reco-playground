package com.acme.reco.api.service;

import java.util.UUID;

public interface PaymentGateway {
    /** Devuelve un id de transacción si “cobra” OK (fake). */
    String charge(int amountCents, String currency, String description, UUID userId);
}
