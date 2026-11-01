package com.chezticket.vendas.reserva.adapter.in.web.dto;

import com.chezticket.vendas.reserva.application.port.in.ItemReservaComando;
import com.chezticket.vendas.reserva.application.port.in.NovaReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CriarReservaRequest(
        @NotNull
        @Schema(description = "ID do participante que está reservando (use 4, do seed de demonstração)",
                example = "4")
        Long participanteId,

        @NotEmpty
        @Valid
        List<ItemReservaRequest> itens) {

    public NovaReserva paraComando() {
        return new NovaReserva(
                participanteId,
                itens.stream().map(i -> new ItemReservaComando(i.tipoIngressoId(), i.quantidade())).toList());
    }
}
