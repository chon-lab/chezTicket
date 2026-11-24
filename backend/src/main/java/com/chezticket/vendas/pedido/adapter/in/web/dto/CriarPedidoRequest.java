package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.pedido.application.port.in.NovoPedido;
import jakarta.validation.constraints.NotNull;

/** O participante vem da sessão autenticada (ver {@code PedidoController}), não do corpo. */
public record CriarPedidoRequest(
        @NotNull
        Long reservaId,
        Long eventoId,
        String codigoCupom) {

    public NovoPedido paraComando(Long participanteId) {
        return new NovoPedido(participanteId, reservaId, eventoId, codigoCupom);
    }
}
