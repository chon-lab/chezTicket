package com.chezticket.shared.domain;

/** Não há uma sessão autenticada válida para a operação. Mapeada para HTTP 401. */
public class NaoAutenticadoException extends DominioException {

    public NaoAutenticadoException() {
        super("Autenticação necessária.");
    }

    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
