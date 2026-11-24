package com.chezticket.vendas.pedido.application.port.out;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dados necessários para abrir uma sessão de checkout no gateway de pagamento.
 *
 * @param pedidoId   vai nos metadados da sessão, para o webhook localizar o pedido de volta
 * @param itens      uma linha por tipo de ingresso
 * @param valorTaxa  taxa de serviço agregada (soma de {@code taxaServico * quantidade} de cada item)
 */
public record SolicitacaoDeCheckout(Long pedidoId, List<ItemDeCheckout> itens, BigDecimal valorTaxa) {
}
