package com.chezticket.vendas.pedido.domain;

import com.chezticket.vendas.pedido.domain.exception.PedidoInvalidoException;
import java.math.BigDecimal;

/** Quantidade de um tipo de ingresso dentro de um pedido, com o preço unitário no momento da compra. */
public record ItemPedido(Long tipoIngressoId, int quantidade, BigDecimal precoUnitario) {

    public ItemPedido {
        if (tipoIngressoId == null) {
            throw new PedidoInvalidoException("O tipo de ingresso do item é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new PedidoInvalidoException("A quantidade de cada item deve ser maior que zero.");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new PedidoInvalidoException("O preço unitário não pode ser negativo.");
        }
    }

    public BigDecimal total() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
