package com.chezticket.vendas.reserva.application.port.out;

import com.chezticket.vendas.reserva.domain.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de reservas. */
public interface ReservaRepositorio {

    Reserva salvar(Reserva reserva);

    Optional<Reserva> buscarPorId(Long id);

    List<Reserva> listarPorParticipante(Long participanteId);

    List<Reserva> listarAtivasExpiradasAte(LocalDateTime referencia);
}
