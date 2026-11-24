package com.chezticket.catalogo.eventocompleto.adapter.in.web.dto;

import com.chezticket.catalogo.eventocompleto.application.NovoEventoCompleto;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Corpo da parte {@code dados} (JSON) de {@code POST /api/eventos/completo}. {@code organizadorId},
 * se enviado pelo front, é ignorado — o organizador é sempre o usuário da sessão autenticada.
 */
public record CriarEventoCompletoRequest(
        @NotNull
        @Schema(example = "1")
        Long categoriaId,

        @NotBlank
        @Size(max = 160)
        String titulo,

        @Size(max = 5000)
        String descricao,

        @Schema(description = "Padrão: LIVRE")
        ClassificacaoEtaria classificacaoEtaria,

        @Schema(description = "Padrão: PADRAO")
        PoliticaReembolso politicaReembolso,

        @NotBlank
        @Size(max = 255)
        @Schema(example = "Av. Maracanã, 229 — Rio de Janeiro/RJ")
        String endereco,

        @NotNull
        @Future
        LocalDateTime inicio,

        @NotNull
        @Future
        LocalDateTime fim,

        List<String> tags,

        Map<String, String> redesSociais,

        @Valid
        CupomCompletoRequest cupom,

        @NotEmpty
        @Valid
        List<IngressoCompletoRequest> ingressos,

        boolean publicar) {

    public NovoEventoCompleto paraComando(Long organizadorId, String imagemCapa, String imagemDivulgacao) {
        return new NovoEventoCompleto(
                organizadorId,
                categoriaId,
                titulo,
                descricao,
                classificacaoEtaria,
                politicaReembolso,
                imagemCapa,
                imagemDivulgacao,
                tags,
                redesSociais,
                endereco,
                inicio,
                fim,
                ingressos.stream().map(IngressoCompletoRequest::paraComando).toList(),
                cupom != null ? cupom.paraComando() : null,
                publicar);
    }
}
