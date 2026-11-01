package com.chezticket.catalogo.sessao.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class SessaoNaoEncontradaException extends RecursoNaoEncontradoException {

    public SessaoNaoEncontradaException(Long id) {
        super("Sessão não encontrada: " + id);
    }
}
