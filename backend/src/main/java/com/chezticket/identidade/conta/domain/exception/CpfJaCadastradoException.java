package com.chezticket.identidade.conta.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

public class CpfJaCadastradoException extends ConflitoException {

    public CpfJaCadastradoException(String cpf) {
        super("Já existe uma conta cadastrada com o CPF \"" + cpf + "\".");
    }
}
