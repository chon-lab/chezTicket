package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.application.port.in.DadosEvento;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public record AtualizarEventoRequest(
        @NotNull
        @Schema(example = "1")
        Long categoriaId,

        @NotBlank
        @Size(max = 160)
        @Schema(example = "Festival de Inverno CEFET/RJ 2026 — edição estendida")
        String titulo,

        @Size(max = 5000)
        String descricao,

        @Schema(example = "DEZ")
        ClassificacaoEtaria classificacaoEtaria,

        @Size(max = 255)
        String imagemCapa,

        @Size(max = 255)
        String imagemDivulgacao,

        @Schema(example = "FLEXIVEL")
        PoliticaReembolso politicaReembolso,

        List<String> tags,

        Map<String, String> redesSociais) {

    public DadosEvento paraComando() {
        return new DadosEvento(categoriaId, titulo, descricao,
                classificacaoEtaria, imagemCapa, imagemDivulgacao, politicaReembolso, tags, redesSociais);
    }
}
