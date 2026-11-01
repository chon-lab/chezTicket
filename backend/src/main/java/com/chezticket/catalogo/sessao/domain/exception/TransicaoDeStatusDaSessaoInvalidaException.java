package com.chezticket.catalogo.sessao.domain.exception;

import com.chezticket.catalogo.sessao.domain.StatusSessao;
import com.chezticket.shared.domain.ConflitoException;

/** Tentativa de mudar o status da sessão para um destino não permitido pelo estado atual. */
public class TransicaoDeStatusDaSessaoInvalidaException extends ConflitoException {

    public TransicaoDeStatusDaSessaoInvalidaException(StatusSessao de, StatusSessao para) {
        super("Não é possível mudar a sessão de " + de + " para " + para + ".");
    }
}
