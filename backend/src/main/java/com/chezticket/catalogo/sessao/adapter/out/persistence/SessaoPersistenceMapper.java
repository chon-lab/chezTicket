package com.chezticket.catalogo.sessao.adapter.out.persistence;

import com.chezticket.catalogo.sessao.domain.Sessao;

final class SessaoPersistenceMapper {

    private SessaoPersistenceMapper() {
    }

    static SessaoJpaEntity paraEntidade(Sessao sessao) {
        return new SessaoJpaEntity(
                sessao.id(),
                sessao.eventoId(),
                sessao.localId(),
                sessao.endereco(),
                sessao.inicio(),
                sessao.fim(),
                sessao.status());
    }

    static Sessao paraDominio(SessaoJpaEntity entidade) {
        return Sessao.reconstituir(
                entidade.getId(),
                entidade.getEventoId(),
                entidade.getLocalId(),
                entidade.getEndereco(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getStatus());
    }
}
