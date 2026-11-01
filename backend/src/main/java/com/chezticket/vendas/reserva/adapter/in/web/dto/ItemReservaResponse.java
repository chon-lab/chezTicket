package com.chezticket.vendas.reserva.adapter.in.web.dto;

import com.chezticket.vendas.reserva.domain.ItemReserva;

public record ItemReservaResponse(Long tipoIngressoId, int quantidade) {

    public static ItemReservaResponse de(ItemReserva item) {
        return new ItemReservaResponse(item.tipoIngressoId(), item.quantidade());
    }
}
