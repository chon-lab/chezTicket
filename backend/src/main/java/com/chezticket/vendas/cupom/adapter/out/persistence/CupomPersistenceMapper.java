package com.chezticket.vendas.cupom.adapter.out.persistence;

import com.chezticket.vendas.cupom.domain.Cupom;

final class CupomPersistenceMapper {

    private CupomPersistenceMapper() {
    }

    static CupomJpaEntity paraEntidade(Cupom cupom) {
        return new CupomJpaEntity(cupom.id(), cupom.eventoId(), cupom.codigo(), cupom.percentualDesconto(),
                cupom.validoAte());
    }

    static Cupom paraDominio(CupomJpaEntity entidade) {
        return Cupom.reconstituir(entidade.getId(), entidade.getEventoId(), entidade.getCodigo(),
                entidade.getPercentualDesconto(), entidade.getValidade());
    }
}
