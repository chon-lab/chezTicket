package com.chezticket.vendas.reserva.domain.exception;

import com.chezticket.shared.domain.ConflitoException;
import com.chezticket.vendas.reserva.domain.StatusReserva;

/** Tentativa de mudar o status da reserva para um destino não permitido pelo estado atual. */
public class TransicaoDeStatusDaReservaInvalidaException extends ConflitoException {

    public TransicaoDeStatusDaReservaInvalidaException(StatusReserva de, StatusReserva para) {
        super("Não é possível mudar a reserva de " + de + " para " + para + ".");
    }
}
