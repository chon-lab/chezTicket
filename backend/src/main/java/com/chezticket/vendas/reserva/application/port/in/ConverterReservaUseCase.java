package com.chezticket.vendas.reserva.application.port.in;

import com.chezticket.vendas.reserva.domain.Reserva;

/** Usado pelo módulo de pedido ao transformar uma reserva ativa numa compra. */
public interface ConverterReservaUseCase {

    Reserva converter(Long reservaId);
}
