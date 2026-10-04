package com.chezticket.catalogo.categoria.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class CategoriaNaoEncontradaException extends RecursoNaoEncontradoException {

    public CategoriaNaoEncontradaException(Long id) {
        super("Categoria não encontrada: " + id);
    }
}
