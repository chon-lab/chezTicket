package com.chezticket.vendas.pedido.adapter.in.web;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Verifica a assinatura ({@code Stripe-Signature}) de um corpo de webhook recebido do Stripe. */
@Component
class StripeWebhookVerifier {

    private final String webhookSecret;

    StripeWebhookVerifier(@Value("${chezticket.stripe.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    Event verificar(String payload, String assinatura) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Webhook do Stripe não configurado (STRIPE_WEBHOOK_SECRET ausente).");
        }
        try {
            return Webhook.constructEvent(payload, assinatura, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assinatura do webhook inválida.");
        }
    }
}
