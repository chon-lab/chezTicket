package com.chezticket.catalogo.sessao.adapter.in.web.dto;

import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CriarSessaoRequest(
        @NotNull
        @Schema(description = "ID do evento ao qual a sessão pertence", example = "1")
        Long eventoId,

        @NotNull
        @Schema(description = "ID do local onde a sessão acontece", example = "1")
        Long localId,

        @Size(max = 255)
        @Schema(description = "Observação de endereço opcional, além do local estruturado",
                example = "Entrada pelos fundos")
        String endereco,

        @NotNull
        @Future
        @Schema(example = "2026-12-05T19:00:00")
        LocalDateTime inicio,

        @NotNull
        @Future
        @Schema(example = "2026-12-05T22:00:00")
        LocalDateTime fim) {

    public NovaSessao paraComando() {
        return new NovaSessao(eventoId, localId, endereco, inicio, fim);
    }
}
