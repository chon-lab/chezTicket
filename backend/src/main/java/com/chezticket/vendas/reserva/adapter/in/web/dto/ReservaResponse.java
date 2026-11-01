package com.chezticket.vendas.reserva.adapter.in.web.dto;

import com.chezticket.vendas.reserva.domain.Reserva;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaResponse(
        Long id,
        Long participanteId,
        List<ItemReservaResponse> itens,
        String status,
        LocalDateTime expiraEm,
        LocalDateTime criadaEm) {

    public static ReservaResponse de(Reserva r) {
        return new ReservaResponse(
                r.id(),
                r.participanteId(),
                r.itens().stream().map(ItemReservaResponse::de).toList(),
                r.status().name(),
                r.expiraEm(),
                r.criadaEm());
    }
}
