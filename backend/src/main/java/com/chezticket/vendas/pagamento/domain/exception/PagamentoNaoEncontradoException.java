package com.chezticket.vendas.pagamento.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class PagamentoNaoEncontradoException extends RecursoNaoEncontradoException {

    public PagamentoNaoEncontradoException(Long pedidoId) {
        super("Nenhum pagamento encontrado para o pedido: " + pedidoId + ".");
    }
}
