package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.pedido.domain.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long participanteId,
        Long reservaId,
        Long cupomId,
        List<ItemPedidoResponse> itens,
        BigDecimal subtotal,
        BigDecimal valorTaxa,
        BigDecimal valorTotal,
        String status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {

    public static PedidoResponse de(Pedido p) {
        return new PedidoResponse(
                p.id(),
                p.participanteId(),
                p.reservaId(),
                p.cupomId(),
                p.itens().stream().map(ItemPedidoResponse::de).toList(),
                p.subtotal(),
                p.valorTaxa(),
                p.valorTotal(),
                p.status().name(),
                p.criadoEm(),
                p.atualizadoEm());
    }
}
