package com.chezticket.catalogo.categoria.adapter.out.persistence;

import com.chezticket.catalogo.categoria.domain.Categoria;

/** Converte entre o modelo de domínio e a entidade JPA. */
final class CategoriaPersistenceMapper {

    private CategoriaPersistenceMapper() {
    }

    static CategoriaJpaEntity paraEntidade(Categoria categoria) {
        return new CategoriaJpaEntity(
                categoria.id(),
                categoria.nome(),
                categoria.slug(),
                categoria.ativa(),
                categoria.criadaEm());
    }

    static Categoria paraDominio(CategoriaJpaEntity entidade) {
        return Categoria.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getSlug(),
                entidade.isAtiva(),
                entidade.getCriadaEm());
    }
}
