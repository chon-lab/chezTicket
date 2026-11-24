package com.chezticket.identidade.conta.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class EmailJaCadastradoException extends ConflitoException {

    public EmailJaCadastradoException(String email) {
        super("Já existe uma conta cadastrada com o e-mail \"" + email + "\".");
    }
}
