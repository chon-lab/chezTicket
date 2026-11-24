package com.chezticket.identidade.conta.domain.exception;

import com.chezticket.shared.domain.NaoAutenticadoException;

/** Mensagem deliberadamente genérica — não revela se o e-mail existe ou se a senha é que está errada. */
public class CredenciaisInvalidasException extends NaoAutenticadoException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}
