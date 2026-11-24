package com.chezticket.vendas.pedido.adapter.out.persistence;

import com.chezticket.vendas.pedido.domain.ItemPedido;
import com.chezticket.vendas.pedido.domain.Pedido;

final class PedidoPersistenceMapper {

    private PedidoPersistenceMapper() {
    }

    static PedidoJpaEntity paraEntidade(Pedido pedido) {
        PedidoJpaEntity entidade = new PedidoJpaEntity(
                pedido.id(),
                pedido.participanteId(),
                pedido.reservaId(),
                pedido.cupomId(),
                pedido.subtotal(),
                pedido.valorTaxa(),
                pedido.valorTotal(),
                pedido.status(),
                pedido.criadoEm(),
                pedido.atualizadoEm());
        for (ItemPedido item : pedido.itens()) {
            entidade.adicionarItem(item.tipoIngressoId(), item.quantidade(), item.precoUnitario());
        }
        return entidade;
    }

    static Pedido paraDominio(PedidoJpaEntity entidade) {
        return Pedido.reconstituir(
                entidade.getId(),
                entidade.getParticipanteId(),
                entidade.getReservaId(),
                entidade.getCupomId(),
                entidade.getItens().stream()
                        .map(item -> new ItemPedido(item.getTipoIngressoId(), item.getQuantidade(),
                                item.getPrecoUnitario()))
                        .toList(),
                entidade.getSubtotal(),
                entidade.getValorTaxa(),
                entidade.getValorTotal(),
                entidade.getStatus(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm());
    }
}
