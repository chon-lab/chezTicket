package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.domain.Pagamento;

final class PagamentoPersistenceMapper {

    private PagamentoPersistenceMapper() {
    }

    static PagamentoJpaEntity paraEntidade(Pagamento pagamento) {
        return new PagamentoJpaEntity(pagamento.id(), pagamento.pedidoId(), pagamento.gateway(),
                pagamento.idTransacao(), pagamento.metodo(), pagamento.parcelas(), pagamento.valor(),
                pagamento.status(), pagamento.criadoEm());
    }

    static Pagamento paraDominio(PagamentoJpaEntity entidade) {
        return Pagamento.reconstituir(entidade.getId(), entidade.getPedidoId(), entidade.getIdTransacao(),
                entidade.getMetodo(), entidade.getParcelas(), entidade.getValor(), entidade.getStatus(),
                entidade.getCriadoEm());
    }
}
