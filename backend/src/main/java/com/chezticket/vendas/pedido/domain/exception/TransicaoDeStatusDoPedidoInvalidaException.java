package com.chezticket.vendas.pedido.domain.exception;

import com.chezticket.shared.domain.ConflitoException;
import com.chezticket.vendas.pedido.domain.StatusPedido;

public class TransicaoDeStatusDoPedidoInvalidaException extends ConflitoException {

    public TransicaoDeStatusDoPedidoInvalidaException(StatusPedido de, StatusPedido para) {
        super("Não é possível mudar o pedido de " + de + " para " + para + ".");
    }
}
