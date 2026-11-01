package com.chezticket.catalogo.ingresso.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class TipoIngressoInvalidoException extends RegraDeNegocioException {

    public TipoIngressoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
