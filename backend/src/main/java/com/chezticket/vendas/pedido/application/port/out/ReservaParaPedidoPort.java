package com.chezticket.vendas.pedido.application.port.out;

import com.chezticket.vendas.reserva.domain.Reserva;

/**
 * Porta de saída: liga o módulo de pedido ao módulo de reserva, reusando os casos de uso
 * públicos de {@code vendas.reserva} (que já concentram as regras de transição de status e
 * liberação de estoque) — único ponto de acoplamento entre os dois módulos.
 */
public interface ReservaParaPedidoPort {

    Reserva buscar(Long reservaId);

    /** Marca a reserva como CONVERTIDA (vira o pedido). Chamado quando o pagamento é confirmado. */
    Reserva converter(Long reservaId);

    /** Cancela a reserva, liberando o estoque retido. Chamado quando o pagamento falha/expira. */
    Reserva cancelar(Long reservaId);
}
