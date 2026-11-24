package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.pedido.domain.Pedido;

/**
 * Chamado pelo webhook do Stripe quando o checkout falha ou expira, liberando o estoque
 * retido pela reserva.
 */
public interface CancelarPedidoUseCase {

    Pedido recusar(Long pedidoId);

    Pedido expirar(Long pedidoId);
}
