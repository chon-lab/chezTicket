package com.chezticket.vendas.reserva.application.port.in;

import com.chezticket.vendas.reserva.domain.Reserva;
import java.util.List;

public interface ConsultarReservasUseCase {

    Reserva buscarPorId(Long id);

    List<Reserva> listarPorParticipante(Long participanteId);
}
