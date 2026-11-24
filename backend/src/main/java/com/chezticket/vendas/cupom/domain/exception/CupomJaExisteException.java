package com.chezticket.vendas.cupom.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class CupomJaExisteException extends ConflitoException {

    public CupomJaExisteException(String codigo) {
        super("Já existe um cupom com o código \"" + codigo + "\".");
    }
}
