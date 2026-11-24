package com.chezticket.vendas.pedido.application.port.out;

import java.math.BigDecimal;

/** Porta de saída: abstrai o gateway de pagamento (Stripe) no estorno de uma cobrança. */
public interface ProcessarReembolsoNoGatewayPort {

    /**
     * @param idTransacaoStripe o {@code PaymentIntent} registrado em {@code Pagamento.idTransacao()}
     * @param valor              valor a estornar (pode ser menor que o total pago, num reembolso parcial)
     */
    void reembolsar(String idTransacaoStripe, BigDecimal valor);
}
