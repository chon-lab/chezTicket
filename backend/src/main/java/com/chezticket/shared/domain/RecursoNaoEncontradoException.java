package com.chezticket.shared.domain;

/** O recurso solicitado não existe. Mapeada para HTTP 404. */
public class RecursoNaoEncontradoException extends DominioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
