package com.chezticket.vendas.pedido.application.port.in;

/**
 * Comando para converter uma reserva ativa em pedido.
 *
 * @param eventoId usado só para validar o cupom (o cupom é por evento); ignorado se
 *                 {@code codigoCupom} não for informado
 */
public record NovoPedido(Long participanteId, Long reservaId, Long eventoId, String codigoCupom) {
}
