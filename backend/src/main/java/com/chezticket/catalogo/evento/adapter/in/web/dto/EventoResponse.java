package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.domain.Evento;
import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        String classificacaoEtaria,
        String classificacaoEtariaDescricao,
        String imagemCapa,
        String status,
        String politicaReembolso,
        String politicaReembolsoDescricao,
        LocalDateTime dataPublicacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {

    public static EventoResponse de(Evento e) {
        return new EventoResponse(
                e.id(),
                e.organizadorId(),
                e.categoriaId(),
                e.titulo(),
                e.descricao(),
                e.classificacaoEtaria().name(),
                e.classificacaoEtaria().descricao(),
                e.imagemCapa(),
                e.status().name(),
                e.politicaReembolso().name(),
                e.politicaReembolso().descricao(),
                e.dataPublicacao(),
                e.criadoEm(),
                e.atualizadoEm());
    }
}
