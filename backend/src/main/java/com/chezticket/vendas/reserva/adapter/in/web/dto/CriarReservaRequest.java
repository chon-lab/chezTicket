package com.chezticket.vendas.reserva.adapter.in.web.dto;

import com.chezticket.vendas.reserva.application.port.in.ItemReservaComando;
import com.chezticket.vendas.reserva.application.port.in.NovaReserva;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record CriarReservaRequest(
        @NotEmpty
        @Valid
        List<ItemReservaRequest> itens,
        Long eventoId,
        @Pattern(regexp = "[A-Za-z0-9]{8}",
                message = "O código do cupom deve conter exatamente 8 caracteres alfanuméricos.")
        String codigoCupom) {

    public NovaReserva paraComando(Long participanteIdEfetivo) {
        return new NovaReserva(
                participanteIdEfetivo,
                itens.stream().map(i -> new ItemReservaComando(i.tipoIngressoId(), i.quantidade())).toList(),
                eventoId,
                codigoCupom);
    }
}
