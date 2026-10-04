package com.chezticket.shared.domain;

/** Uma regra de negócio foi violada (ex.: dado inválido). Mapeada para HTTP 422. */
public class RegraDeNegocioException extends DominioException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
