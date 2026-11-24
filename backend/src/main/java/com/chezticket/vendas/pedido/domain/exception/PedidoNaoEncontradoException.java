package com.chezticket.vendas.pedido.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class PedidoNaoEncontradoException extends RecursoNaoEncontradoException {

    public PedidoNaoEncontradoException(Long id) {
        super("Pedido não encontrado: " + id);
    }
}
