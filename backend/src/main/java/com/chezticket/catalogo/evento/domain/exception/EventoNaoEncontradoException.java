package com.chezticket.catalogo.evento.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class EventoNaoEncontradoException extends RecursoNaoEncontradoException {

    public EventoNaoEncontradoException(Long id) {
        super("Evento não encontrado: " + id);
    }
}
