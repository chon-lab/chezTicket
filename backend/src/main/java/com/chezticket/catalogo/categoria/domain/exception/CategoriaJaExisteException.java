package com.chezticket.catalogo.categoria.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class CategoriaJaExisteException extends ConflitoException {

    public CategoriaJaExisteException(String nome) {
        super("Já existe uma categoria com o nome \"" + nome + "\".");
    }
}
