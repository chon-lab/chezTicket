package com.chezticket.vendas.reserva.application.port.in;

/** Item de uma nova reserva: tipo de ingresso + quantidade desejada. */
public record ItemReservaComando(Long tipoIngressoId, int quantidade) {
}
