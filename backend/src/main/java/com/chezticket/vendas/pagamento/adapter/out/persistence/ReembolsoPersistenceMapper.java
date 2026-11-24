package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.domain.Reembolso;

final class ReembolsoPersistenceMapper {

    private ReembolsoPersistenceMapper() {
    }

    static ReembolsoJpaEntity paraEntidade(Reembolso reembolso) {
        return new ReembolsoJpaEntity(reembolso.id(), reembolso.pagamentoId(), reembolso.motivo(), reembolso.tipo(),
                reembolso.valor(), reembolso.status(), reembolso.solicitadoEm(), reembolso.processadoEm());
    }

    static Reembolso paraDominio(ReembolsoJpaEntity entidade) {
        return Reembolso.reconstituir(entidade.getId(), entidade.getPagamentoId(), entidade.getMotivo(),
                entidade.getTipo(), entidade.getValor(), entidade.getStatus(), entidade.getSolicitadoEm(),
                entidade.getProcessadoEm());
    }
}
