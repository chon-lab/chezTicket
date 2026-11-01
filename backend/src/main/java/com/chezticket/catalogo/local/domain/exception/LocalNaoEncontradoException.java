package com.chezticket.catalogo.local.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class LocalNaoEncontradoException extends RecursoNaoEncontradoException {

    public LocalNaoEncontradoException(Long id) {
        super("Local não encontrado: " + id);
    }
}
