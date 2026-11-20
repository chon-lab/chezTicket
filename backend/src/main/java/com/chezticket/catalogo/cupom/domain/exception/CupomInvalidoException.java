package com.chezticket.catalogo.cupom.domain.exception;

import com.chezticket.shared.domain.RegraDeNegocioException;

public class CupomInvalidoException extends RegraDeNegocioException {

    public CupomInvalidoException() {
        super("Cupom inválido, expirado ou não disponível para este evento.");
    }
}
