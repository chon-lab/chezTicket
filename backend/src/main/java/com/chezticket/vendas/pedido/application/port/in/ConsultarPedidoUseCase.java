package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.ingresso.domain.Ingresso;
import com.chezticket.vendas.pedido.domain.Pedido;
import java.util.List;

public interface ConsultarPedidoUseCase {

    Pedido buscarPorId(Long id);

    List<Pedido> listarPorParticipante(Long participanteId);

    /** Ingressos já emitidos para o pedido — vazio enquanto o pagamento está pendente. */
    List<Ingresso> listarIngressosDoPedido(Long pedidoId);
}
