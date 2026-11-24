package com.chezticket.vendas.ingresso.application.port.out;

import com.chezticket.vendas.ingresso.domain.Ingresso;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de ingressos emitidos. */
public interface IngressoRepositorio {

    Ingresso salvar(Ingresso ingresso);

    Optional<Ingresso> buscarPorId(Long id);

    List<Ingresso> listarPorPedido(Long pedidoId);
}
