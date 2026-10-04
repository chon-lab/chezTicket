package com.chezticket.catalogo.evento.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class EventoInvalidoException extends RegraDeNegocioException {

    public EventoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
