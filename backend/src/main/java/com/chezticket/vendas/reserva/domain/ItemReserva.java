package com.chezticket.vendas.reserva.domain;

import com.chezticket.vendas.reserva.domain.exception.ReservaInvalidaException;

/** Quantidade retida de um tipo de ingresso dentro de uma reserva. Objeto de valor. */
public record ItemReserva(Long tipoIngressoId, int quantidade) {

    public ItemReserva {
        if (tipoIngressoId == null) {
            throw new ReservaInvalidaException("O tipo de ingresso do item é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new ReservaInvalidaException("A quantidade de cada item deve ser maior que zero.");
        }
    }
}
