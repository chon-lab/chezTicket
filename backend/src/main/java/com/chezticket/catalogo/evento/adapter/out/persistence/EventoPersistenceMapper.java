package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.domain.Evento;

final class EventoPersistenceMapper {

    private EventoPersistenceMapper() {
    }

    static EventoJpaEntity paraEntidade(Evento evento) {
        return new EventoJpaEntity(
                evento.id(),
                evento.organizadorId(),
                evento.categoriaId(),
                evento.titulo(),
                evento.descricao(),
                evento.classificacaoEtaria(),
                evento.imagemCapa(),
                evento.status(),
                evento.politicaReembolso(),
                evento.dataPublicacao(),
                evento.criadoEm(),
                evento.atualizadoEm());
    }

    static Evento paraDominio(EventoJpaEntity entidade) {
        return Evento.reconstituir(
                entidade.getId(),
                entidade.getOrganizadorId(),
                entidade.getCategoriaId(),
                entidade.getTitulo(),
                entidade.getDescricao(),
                entidade.getClassificacaoEtaria(),
                entidade.getImagemCapa(),
                entidade.getStatus(),
                entidade.getPoliticaReembolso(),
                entidade.getDataPublicacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm());
    }
}
