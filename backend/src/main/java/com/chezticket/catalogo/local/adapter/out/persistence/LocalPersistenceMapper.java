package com.chezticket.catalogo.local.adapter.out.persistence;

import com.chezticket.catalogo.local.domain.Local;

final class LocalPersistenceMapper {

    private LocalPersistenceMapper() {
    }

    static LocalJpaEntity paraEntidade(Local local) {
        return new LocalJpaEntity(
                local.id(),
                local.nome(),
                local.endereco(),
                local.cidade(),
                local.uf(),
                local.capacidadeTotal(),
                local.criadoEm());
    }

    static Local paraDominio(LocalJpaEntity entidade) {
        return Local.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getEndereco(),
                entidade.getCidade(),
                entidade.getUf(),
                entidade.getCapacidadeTotal(),
                entidade.getCriadoEm());
    }
}
