package com.chezticket.vendas.reserva.application.port.in;

import java.util.List;

/** Comando para reservar um ou mais tipos de ingresso em nome de um participante. */
public record NovaReserva(
        Long participanteId,
        List<ItemReservaComando> itens,
        Long eventoId,
        String codigoCupom) {

    public NovaReserva(Long participanteId, List<ItemReservaComando> itens) {
        this(participanteId, itens, null, null);
    }
}
