package com.chezticket.vendas.pagamento.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class PagamentoInvalidoException extends RegraDeNegocioException {

    public PagamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
