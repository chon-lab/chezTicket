package com.chezticket.vendas.pedido.adapter.out.reserva;

import com.chezticket.vendas.pedido.application.port.out.ReservaParaPedidoPort;
import com.chezticket.vendas.reserva.application.port.in.CancelarReservaUseCase;
import com.chezticket.vendas.reserva.application.port.in.ConsultarReservasUseCase;
import com.chezticket.vendas.reserva.application.port.in.ConverterReservaUseCase;
import com.chezticket.vendas.reserva.application.port.in.EstenderReservaUseCase;
import com.chezticket.vendas.reserva.domain.Reserva;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de pedido ao módulo de reserva, reusando os casos de
 * uso públicos de {@code vendas.reserva} (que já concentram as regras de transição de status
 * e liberação de estoque).
 */
@Component
class ReservaParaPedidoAdapter implements ReservaParaPedidoPort {

    private final ConsultarReservasUseCase consultarReservas;
    private final ConverterReservaUseCase converterReserva;
    private final CancelarReservaUseCase cancelarReserva;
    private final EstenderReservaUseCase estenderReserva;

    ReservaParaPedidoAdapter(ConsultarReservasUseCase consultarReservas, ConverterReservaUseCase converterReserva,
            CancelarReservaUseCase cancelarReserva, EstenderReservaUseCase estenderReserva) {
        this.consultarReservas = consultarReservas;
        this.converterReserva = converterReserva;
        this.cancelarReserva = cancelarReserva;
        this.estenderReserva = estenderReserva;
    }

    @Override
    public Reserva buscar(Long reservaId) {
        return consultarReservas.buscarPorId(reservaId);
    }

    @Override
    public Reserva converter(Long reservaId) {
        return converterReserva.converter(reservaId);
    }

    @Override
    public Reserva cancelar(Long reservaId) {
        return cancelarReserva.cancelar(reservaId);
    }

    @Override
    public Reserva estenderExpiracao(Long reservaId, LocalDateTime novaExpiracao) {
        return estenderReserva.estenderExpiracao(reservaId, novaExpiracao);
    }
}
