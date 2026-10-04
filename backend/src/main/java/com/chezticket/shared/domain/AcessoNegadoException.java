package com.chezticket.shared.domain;

/** O solicitante não tem permissão para executar a operação (HTTP 403). */
public abstract class AcessoNegadoException extends DominioException {

    protected AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
