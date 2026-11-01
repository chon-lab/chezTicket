package com.chezticket.catalogo.local.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class LocalInvalidoException extends RegraDeNegocioException {

    public LocalInvalidoException(String mensagem) {
        super(mensagem);
    }
}
