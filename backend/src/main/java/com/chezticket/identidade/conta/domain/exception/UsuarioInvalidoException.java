package com.chezticket.identidade.conta.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class UsuarioInvalidoException extends RegraDeNegocioException {

    public UsuarioInvalidoException(String mensagem) {
        super(mensagem);
    }
}
