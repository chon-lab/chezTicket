package com.chezticket.catalogo.evento.domain.exception;

import com.chezticket.catalogo.evento.domain.StatusEvento;
import com.chezticket.shared.domain.AcessoNegadoException;

/** Só administradores podem remover um evento que já saiu de RASCUNHO. */
public class RemocaoNaoAutorizadaException extends AcessoNegadoException {

    public RemocaoNaoAutorizadaException(Long eventoId, StatusEvento status) {
        super("Apenas administradores podem remover um evento " + status
                + " (evento " + eventoId + "). Organizadores só removem rascunhos — "
                + "use /cancelamento para encerrar um evento publicado.");
    }
}
