package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public record CriarEventoRequest(
        @NotNull
        @Schema(description = "ID do organizador responsável", example = "2")
        Long organizadorId,

        @NotNull
        @Schema(description = "ID da categoria do catálogo", example = "1")
        Long categoriaId,

        @NotBlank
        @Size(max = 160)
        @Schema(example = "Festival de Inverno CEFET/RJ 2026")
        String titulo,

        @Size(max = 5000)
        @Schema(example = "Três dias de shows, oficinas e feira de tecnologia no campus Maracanã.")
        String descricao,

        @Schema(description = "Padrão: LIVRE", example = "LIVRE")
        ClassificacaoEtaria classificacaoEtaria,

        @Size(max = 255)
        @Schema(example = "https://cdn.chezticket.dev/capas/festival-inverno.jpg")
        String imagemCapa,

        @Schema(description = "Padrão: PADRAO", example = "PADRAO")
        PoliticaReembolso politicaReembolso,

        @Size(max = 20)
        List<@NotBlank @Size(max = 80) String> tags,

        Map<@NotBlank @Size(max = 20) String, @NotBlank @Size(max = 2048) String> redesSociais) {

    public NovoEvento paraComando() {
        return new NovoEvento(organizadorId, categoriaId, titulo, descricao,
                classificacaoEtaria, imagemCapa, null, tags, redesSociais, politicaReembolso);
    }

    public CriarEventoRequest(Long organizadorId, Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa,
            PoliticaReembolso politicaReembolso) {
        this(organizadorId, categoriaId, titulo, descricao, classificacaoEtaria, imagemCapa,
                politicaReembolso, List.of(), Map.of());
    }
}
