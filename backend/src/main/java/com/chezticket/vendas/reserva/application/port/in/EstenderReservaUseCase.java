package com.chezticket.vendas.reserva.application.port.in;

import com.chezticket.vendas.reserva.domain.Reserva;
import java.time.LocalDateTime;

public interface EstenderReservaUseCase {

    /** Adia o prazo de expiração da reserva (nunca o antecipa). Veja {@code Reserva.estenderExpiracao}. */
    Reserva estenderExpiracao(Long id, LocalDateTime novaExpiracao);
}
