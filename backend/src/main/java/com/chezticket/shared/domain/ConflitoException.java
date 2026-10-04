package com.chezticket.shared.domain;

/** A operação conflita com o estado atual (ex.: duplicidade). Mapeada para HTTP 409. */
public class ConflitoException extends DominioException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
