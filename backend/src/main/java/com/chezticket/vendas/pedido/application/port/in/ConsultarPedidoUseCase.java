package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.pedido.domain.Pedido;
import java.util.List;

public interface ConsultarPedidoUseCase {

    Pedido buscarPorId(Long id);

    List<Pedido> listarPorParticipante(Long participanteId);
}
