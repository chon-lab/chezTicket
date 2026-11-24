package com.chezticket.vendas.cupom.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class CupomInvalidoException extends RegraDeNegocioException {

    public CupomInvalidoException(String mensagem) {
        super(mensagem);
    }
}
