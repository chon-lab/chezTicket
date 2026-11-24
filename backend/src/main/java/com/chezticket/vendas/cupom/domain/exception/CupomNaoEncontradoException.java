package com.chezticket.vendas.cupom.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class CupomNaoEncontradoException extends RecursoNaoEncontradoException {

    public CupomNaoEncontradoException(String codigo) {
        super("Cupom \"" + codigo + "\" não encontrado para este evento.");
    }
}
