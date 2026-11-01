package com.chezticket.vendas.reserva.application.port.in;

import com.chezticket.vendas.reserva.domain.Reserva;

public interface CancelarReservaUseCase {

    Reserva cancelar(Long id);
}
