package com.chezticket.vendas.cupom.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class CupomExpiradoException extends ConflitoException {

    public CupomExpiradoException(String codigo) {
        super("O cupom \"" + codigo + "\" expirou.");
    }
}
