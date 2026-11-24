package com.chezticket.vendas.pedido.application.port.out;

import java.math.BigDecimal;

/** Uma linha do checkout: nome exibido ao comprador, preço unitário e quantidade. */
public record ItemDeCheckout(String nome, BigDecimal valorUnitario, int quantidade) {
}
