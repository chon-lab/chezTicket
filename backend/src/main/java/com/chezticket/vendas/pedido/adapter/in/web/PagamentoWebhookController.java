package com.chezticket.vendas.pedido.adapter.in.web;

import com.chezticket.vendas.pagamento.domain.MetodoPagamento;
import com.chezticket.vendas.pedido.application.port.in.CancelarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.ConfirmarPagamentoUseCase;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Recebe os eventos do Stripe Checkout. {@code checkout.session.completed} confirma o pagamento
 * e emite os ingressos; {@code checkout.session.expired} libera o estoque retido pela reserva.
 * Outros tipos de evento são ignorados (200 OK, sem efeito) — é assim que o Stripe espera que um
 * endpoint de webhook trate eventos que não interessam ao consumidor.
 */
@Tag(name = "Pagamentos", description = "Webhook do Stripe")
@RestController
@RequestMapping("/api/pagamentos")
class PagamentoWebhookController {

    private static final String EVENTO_CHECKOUT_CONCLUIDO = "checkout.session.completed";
    private static final String EVENTO_CHECKOUT_EXPIRADO = "checkout.session.expired";

    /**
     * Checkout Sessions não expõem o método de pagamento de forma direta sem expandir o
     * payment_intent — e o projeto só habilita cartão no Stripe por enquanto, então fixar aqui
     * evita uma chamada extra à API só para redescobrir o que já se sabe.
     */
    private static final MetodoPagamento METODO_PADRAO = MetodoPagamento.CARTAO_CREDITO;

    private final StripeWebhookVerifier verifier;
    private final ConfirmarPagamentoUseCase confirmarPagamento;
    private final CancelarPedidoUseCase cancelarPedido;

    PagamentoWebhookController(StripeWebhookVerifier verifier, ConfirmarPagamentoUseCase confirmarPagamento,
            CancelarPedidoUseCase cancelarPedido) {
        this.verifier = verifier;
        this.confirmarPagamento = confirmarPagamento;
        this.cancelarPedido = cancelarPedido;
    }

    @Operation(summary = "Webhook do Stripe Checkout", description = "Chamado só pelo Stripe, nunca pelo front.")
    @PostMapping("/webhook")
    ResponseEntity<Void> webhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String assinatura) {
        Event event = verifier.verificar(payload, assinatura);
        switch (event.getType()) {
            case EVENTO_CHECKOUT_CONCLUIDO -> tratarCheckoutConcluido(extrairSession(event));
            case EVENTO_CHECKOUT_EXPIRADO -> cancelarPedido.expirar(pedidoIdDaSessao(extrairSession(event)));
            default -> { /* evento sem interesse para este webhook */ }
        }
        return ResponseEntity.ok().build();
    }

    private void tratarCheckoutConcluido(Session session) {
        if (!"paid".equals(session.getPaymentStatus())) {
            return;
        }
        confirmarPagamento.confirmarPagamento(pedidoIdDaSessao(session), session.getPaymentIntent(), METODO_PADRAO);
    }

    private static Long pedidoIdDaSessao(Session session) {
        String valor = session.getMetadata() == null ? null : session.getMetadata().get("pedidoId");
        if (valor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sessão sem metadado pedidoId.");
        }
        return Long.valueOf(valor);
    }

    private static Session extrairSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject objeto = deserializer.getObject().orElse(null);
        if (objeto == null) {
            try {
                objeto = deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Não foi possível interpretar o evento do Stripe.");
            }
        }
        if (!(objeto instanceof Session session)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload de evento inesperado.");
        }
        return session;
    }
}
