package com.chezticket.vendas.reserva.adapter.in.web.dto;

import com.chezticket.vendas.reserva.application.port.in.ItemReservaComando;
import com.chezticket.vendas.reserva.application.port.in.NovaReserva;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** O participante vem da sessão autenticada (ver {@code ReservaController}), não do corpo. */
public record CriarReservaRequest(
        @NotEmpty
        @Valid
        List<ItemReservaRequest> itens) {

    public NovaReserva paraComando(Long participanteId) {
        return new NovaReserva(
                participanteId,
                itens.stream().map(i -> new ItemReservaComando(i.tipoIngressoId(), i.quantidade())).toList());
    }
}
