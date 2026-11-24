package com.chezticket.vendas.pedido.adapter.out.stripe;

import com.chezticket.vendas.pagamento.domain.exception.FalhaNoGatewayDePagamentoException;
import com.chezticket.vendas.pedido.application.port.out.CriarSessaoDeCheckoutPort;
import com.chezticket.vendas.pedido.application.port.out.ItemDeCheckout;
import com.chezticket.vendas.pedido.application.port.out.SolicitacaoDeCheckout;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que abre uma sessão de checkout no Stripe (modo hospedado — o
 * comprador é redirecionado para o domínio do Stripe e volta ao front em {@code success_url}/
 * {@code cancel_url}). O {@code pedidoId} vai nos metadados da sessão: é assim que o webhook
 * ({@code checkout.session.completed}) localiza o pedido de volta, sem precisar guardar o id
 * da sessão Stripe no banco.
 */
@Component
class StripeCheckoutAdapter implements CriarSessaoDeCheckoutPort {

    private static final String MOEDA = "brl";

    /** Mínimo que o Stripe aceita para {@code expires_at}, a partir da criação da sessão. */
    private static final Duration VALIDADE_MINIMA = Duration.ofMinutes(30);

    private final String secretKey;
    private final String frontendUrl;

    StripeCheckoutAdapter(@Value("${chezticket.stripe.secret-key:}") String secretKey,
            @Value("${chezticket.frontend-url}") String frontendUrl) {
        this.secretKey = secretKey;
        this.frontendUrl = frontendUrl == null ? "" : frontendUrl.strip().replaceAll("/+$", "");
    }

    @Override
    public String criar(SolicitacaoDeCheckout solicitacao) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new FalhaNoGatewayDePagamentoException(
                    "O pagamento online não está configurado no momento (chave do Stripe ausente).");
        }

        Duration validade = solicitacao.validade() == null || solicitacao.validade().compareTo(VALIDADE_MINIMA) < 0
                ? VALIDADE_MINIMA
                : solicitacao.validade();

        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/#/pedido-sucesso?pedidoId=" + solicitacao.pedidoId())
                .setCancelUrl(frontendUrl + "/#/pedido-cancelado?pedidoId=" + solicitacao.pedidoId())
                .setExpiresAt(Instant.now().plus(validade).getEpochSecond())
                .putMetadata("pedidoId", String.valueOf(solicitacao.pedidoId()));

        for (ItemDeCheckout item : solicitacao.itens()) {
            params.addLineItem(lineItem(item.nome(), item.valorUnitario(), item.quantidade()));
        }
        if (solicitacao.valorTaxa().compareTo(BigDecimal.ZERO) > 0) {
            params.addLineItem(lineItem("Taxa de serviço", solicitacao.valorTaxa(), 1));
        }

        try {
            Session session = Session.create(params.build(), RequestOptions.builder().setApiKey(secretKey).build());
            return session.getUrl();
        } catch (StripeException e) {
            throw new FalhaNoGatewayDePagamentoException("Falha ao abrir o checkout: " + e.getMessage());
        }
    }

    private static SessionCreateParams.LineItem lineItem(String nome, BigDecimal valorUnitario, int quantidade) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) quantidade)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(MOEDA)
                        .setUnitAmount(emCentavos(valorUnitario))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(nome)
                                .build())
                        .build())
                .build();
    }

    private static long emCentavos(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }
}
