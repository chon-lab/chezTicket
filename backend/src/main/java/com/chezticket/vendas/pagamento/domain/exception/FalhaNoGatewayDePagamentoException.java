package com.chezticket.vendas.pagamento.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

/** O gateway de pagamento (Stripe) recusou ou falhou ao processar a operação. */
public class FalhaNoGatewayDePagamentoException extends ConflitoException {

    public FalhaNoGatewayDePagamentoException(String mensagem) {
        super(mensagem);
    }
}
