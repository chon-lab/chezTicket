package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.pedido.domain.ItemPedido;
import java.math.BigDecimal;

public record ItemPedidoResponse(Long tipoIngressoId, int quantidade, BigDecimal precoUnitario) {

    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(item.tipoIngressoId(), item.quantidade(), item.precoUnitario());
    }
}
