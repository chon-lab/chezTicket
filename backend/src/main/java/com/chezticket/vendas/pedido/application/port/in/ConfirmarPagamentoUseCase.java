package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.pagamento.domain.MetodoPagamento;

/** Chamado pelo webhook do Stripe quando o checkout é confirmado ({@code checkout.session.completed}). */
public interface ConfirmarPagamentoUseCase {

    ResultadoPedido confirmarPagamento(Long pedidoId, String idTransacaoStripe, MetodoPagamento metodo);
}
