package com.chezticket.vendas.pedido.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class PedidoInvalidoException extends RegraDeNegocioException {

    public PedidoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
