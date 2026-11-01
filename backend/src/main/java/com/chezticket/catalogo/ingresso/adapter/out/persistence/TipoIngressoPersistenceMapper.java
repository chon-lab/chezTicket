package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;

final class TipoIngressoPersistenceMapper {

    private TipoIngressoPersistenceMapper() {
    }

    static TipoIngressoJpaEntity paraEntidade(TipoIngresso tipoIngresso) {
        return new TipoIngressoJpaEntity(
                tipoIngresso.id(),
                tipoIngresso.sessaoId(),
                tipoIngresso.nome(),
                tipoIngresso.gratuito(),
                tipoIngresso.preco(),
                tipoIngresso.taxaServico(),
                tipoIngresso.quantidadeTotal(),
                tipoIngresso.quantidadeVendida(),
                tipoIngresso.limitePorCompra(),
                tipoIngresso.versao());
    }

    static TipoIngresso paraDominio(TipoIngressoJpaEntity entidade) {
        return TipoIngresso.reconstituir(
                entidade.getId(),
                entidade.getSessaoId(),
                entidade.getNome(),
                entidade.isGratuito(),
                entidade.getPreco(),
                entidade.getTaxaServico(),
                entidade.getQuantidadeTotal(),
                entidade.getQuantidadeVendida(),
                entidade.getLimitePorCompra(),
                entidade.getVersao());
    }
}
