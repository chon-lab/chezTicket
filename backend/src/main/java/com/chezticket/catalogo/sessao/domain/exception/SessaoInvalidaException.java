package com.chezticket.catalogo.sessao.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class SessaoInvalidaException extends RegraDeNegocioException {

    public SessaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
