package com.chezticket.vendas.pedido.application.port.in;

public interface CriarPedidoUseCase {

    /**
     * Converte a reserva ativa do participante em um pedido. Se o total for zero (ingressos
     * gratuitos), confirma e emite os ingressos na hora; senão, abre uma sessão de checkout
     * Stripe e devolve a URL de pagamento.
     */
    ResultadoPedido criar(NovoPedido comando);
}
