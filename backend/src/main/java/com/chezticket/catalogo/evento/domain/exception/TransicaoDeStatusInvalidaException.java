package com.chezticket.catalogo.evento.domain.exception;

import com.chezticket.catalogo.evento.domain.StatusEvento;
import com.chezticket.shared.domain.ConflitoException;

/** Tentativa de mudar o status do evento para um destino não permitido pelo estado atual. */
public class TransicaoDeStatusInvalidaException extends ConflitoException {

    public TransicaoDeStatusInvalidaException(StatusEvento de, StatusEvento para) {
        super("Não é possível mudar o evento de " + de + " para " + para + ".");
    }
}
