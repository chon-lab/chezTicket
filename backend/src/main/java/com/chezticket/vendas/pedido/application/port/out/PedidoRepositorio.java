package com.chezticket.vendas.pedido.application.port.out;

import com.chezticket.vendas.pedido.domain.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepositorio {

    Pedido salvar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);

    List<Pedido> listarPorParticipante(Long participanteId);
}
