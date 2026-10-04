package com.chezticket.catalogo.categoria.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class CategoriaInvalidaException extends RegraDeNegocioException {

    public CategoriaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
