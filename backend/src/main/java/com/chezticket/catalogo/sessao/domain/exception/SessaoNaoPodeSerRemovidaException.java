package com.chezticket.catalogo.sessao.domain.exception;

import com.chezticket.catalogo.sessao.domain.StatusSessao;
import com.chezticket.shared.domain.ConflitoException;

/** Só uma sessão ainda AGENDADA pode ser removida. */
public class SessaoNaoPodeSerRemovidaException extends ConflitoException {

    public SessaoNaoPodeSerRemovidaException(Long sessaoId, StatusSessao status) {
        super("Só é possível remover uma sessão AGENDADA (sessão " + sessaoId + " está " + status
                + "). Use /cancelamento para desistir de uma sessão em andamento.");
    }
}
