package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.shared.web.UploadUrlResolver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record EventoResponse(
        Long id,
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        String classificacaoEtaria,
        String classificacaoEtariaDescricao,
        String imagemCapa,
        String imagemDivulgacao,
        String status,
        String politicaReembolso,
        String politicaReembolsoDescricao,
        List<String> tags,
        Map<String, String> redesSociais,
        LocalDateTime dataPublicacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {

    public static EventoResponse de(Evento e, UploadUrlResolver uploads) {
        return new EventoResponse(
                e.id(),
                e.organizadorId(),
                e.categoriaId(),
                e.titulo(),
                e.descricao(),
                e.classificacaoEtaria().name(),
                e.classificacaoEtaria().descricao(),
                uploads.resolver(e.imagemCapa()),
                uploads.resolver(e.imagemDivulgacao()),
                e.status().name(),
                e.politicaReembolso().name(),
                e.politicaReembolso().descricao(),
                e.tags(),
                e.redesSociais(),
                e.dataPublicacao(),
                e.criadoEm(),
                e.atualizadoEm());
    }
}
