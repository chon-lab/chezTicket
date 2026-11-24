package com.chezticket.vendas.pedido.adapter.out.stripe;

import com.chezticket.vendas.pagamento.domain.exception.FalhaNoGatewayDePagamentoException;
import com.chezticket.vendas.pedido.application.port.out.ProcessarReembolsoNoGatewayPort;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.RefundCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Adaptador de saída que estorna uma cobrança no Stripe a partir do PaymentIntent original. */
@Component
class StripeRefundAdapter implements ProcessarReembolsoNoGatewayPort {

    private final String secretKey;

    StripeRefundAdapter(@Value("${chezticket.stripe.secret-key:}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void reembolsar(String idTransacaoStripe, BigDecimal valor) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new FalhaNoGatewayDePagamentoException(
                    "O reembolso online não está configurado no momento (chave do Stripe ausente).");
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(idTransacaoStripe)
                .setAmount(emCentavos(valor))
                .build();

        try {
            Refund.create(params, RequestOptions.builder().setApiKey(secretKey).build());
        } catch (StripeException e) {
            throw new FalhaNoGatewayDePagamentoException("Falha ao processar o estorno: " + e.getMessage());
        }
    }

    private static long emCentavos(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }
}
