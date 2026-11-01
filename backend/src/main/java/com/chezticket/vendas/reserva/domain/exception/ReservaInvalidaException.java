package com.chezticket.vendas.reserva.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class ReservaInvalidaException extends RegraDeNegocioException {

    public ReservaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
