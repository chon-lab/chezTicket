package com.chezticket.catalogo.ingresso.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class TipoIngressoNaoEncontradoException extends RecursoNaoEncontradoException {

    public TipoIngressoNaoEncontradoException(Long id) {
        super("Tipo de ingresso não encontrado: " + id);
    }
}
