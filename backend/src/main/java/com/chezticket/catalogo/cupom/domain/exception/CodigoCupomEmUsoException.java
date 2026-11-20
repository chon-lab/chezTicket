package com.chezticket.catalogo.cupom.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class CodigoCupomEmUsoException extends ConflitoException {

    public CodigoCupomEmUsoException() {
        super("Este código de cupom já está em uso.");
    }
}
