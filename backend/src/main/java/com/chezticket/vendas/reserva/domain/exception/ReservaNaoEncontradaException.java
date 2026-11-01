package com.chezticket.vendas.reserva.domain.exception;

import com.chezticket.shared.domain.RecursoNaoEncontradoException;

public class ReservaNaoEncontradaException extends RecursoNaoEncontradoException {

    public ReservaNaoEncontradaException(Long id) {
        super("Reserva não encontrada: " + id);
    }
}
