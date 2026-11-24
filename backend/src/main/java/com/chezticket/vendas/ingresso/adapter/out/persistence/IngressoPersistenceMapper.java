package com.chezticket.vendas.ingresso.adapter.out.persistence;

import com.chezticket.vendas.ingresso.domain.Ingresso;

final class IngressoPersistenceMapper {

    private IngressoPersistenceMapper() {
    }

    static IngressoJpaEntity paraEntidade(Ingresso ingresso) {
        return new IngressoJpaEntity(ingresso.id(), ingresso.pedidoId(), ingresso.tipoIngressoId(),
                ingresso.codigoQr(), ingresso.status(), ingresso.titular(), ingresso.dataEmissao());
    }

    static Ingresso paraDominio(IngressoJpaEntity entidade) {
        return Ingresso.reconstituir(entidade.getId(), entidade.getPedidoId(), entidade.getTipoIngressoId(),
                entidade.getCodigoQr(), entidade.getStatus(), entidade.getTitular(), entidade.getDataEmissao());
    }
}
