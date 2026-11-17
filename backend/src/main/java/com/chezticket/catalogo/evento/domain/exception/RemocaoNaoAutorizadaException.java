package com.chezticket.catalogo.evento.domain.exception;

import com.chezticket.catalogo.evento.domain.StatusEvento;
import com.chezticket.shared.domain.AcessoNegadoException;

/** A remoção é restrita ao organizador do evento ou a um administrador. */
public class RemocaoNaoAutorizadaException extends AcessoNegadoException {

    public RemocaoNaoAutorizadaException(Long eventoId, StatusEvento status) {
        super("O usuário não tem permissão para remover o evento " + eventoId + " (" + status + ").");
    }
}
