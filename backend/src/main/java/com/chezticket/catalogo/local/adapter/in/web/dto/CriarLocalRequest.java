package com.chezticket.catalogo.local.adapter.in.web.dto;

import com.chezticket.catalogo.local.application.port.in.NovoLocal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CriarLocalRequest(
        @NotBlank
        @Size(max = 160)
        @Schema(example = "Auditório Principal — CEFET/RJ Maracanã")
        String nome,

        @NotBlank
        @Size(max = 255)
        @Schema(example = "Av. Maracanã, 229")
        String endereco,

        @NotBlank
        @Size(max = 120)
        @Schema(example = "Rio de Janeiro")
        String cidade,

        @NotBlank
        @Size(min = 2, max = 2)
        @Schema(example = "RJ")
        String uf,

        @Positive
        @Schema(example = "400")
        int capacidadeTotal) {

    public NovoLocal paraComando() {
        return new NovoLocal(nome, endereco, cidade, uf, capacidadeTotal);
    }
}
