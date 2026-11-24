package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.pagamento.domain.MotivoReembolso;
import com.chezticket.vendas.pagamento.domain.TipoReembolso;
import com.chezticket.vendas.pedido.domain.Pedido;
import java.math.BigDecimal;

public interface SolicitarReembolsoUseCase {

    /**
     * Estorna o pagamento de um pedido PAGO ou CONCLUIDO, pelo valor total já pago (quando
     * {@code tipo} é {@link TipoReembolso#TOTAL}, {@code valor} é ignorado) ou por um valor
     * menor (reembolso parcial).
     *
     * @throws com.chezticket.vendas.pedido.domain.exception.PedidoInvalidoException pedido não
     *         pertence ao participante, ou valor parcial inválido/maior que o pago
     * @throws com.chezticket.vendas.pedido.domain.exception.TransicaoDeStatusDoPedidoInvalidaException
     *         pedido não está PAGO nem CONCLUIDO
     * @throws com.chezticket.vendas.pagamento.domain.exception.PagamentoNaoEncontradoException
     *         pedido sem pagamento registrado (nunca foi cobrado via Stripe)
     * @throws com.chezticket.vendas.pagamento.domain.exception.FalhaNoGatewayDePagamentoException
     *         o Stripe recusou ou falhou ao processar o estorno
     */
    Pedido solicitarReembolso(Long pedidoId, Long participanteId, MotivoReembolso motivo, TipoReembolso tipo,
            BigDecimal valor);
}
