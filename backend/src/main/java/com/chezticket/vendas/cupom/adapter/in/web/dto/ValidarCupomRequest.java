package com.chezticket.vendas.cupom.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidarCupomRequest(
        @NotNull
        @Schema(example = "1")
        Long eventoId,

        @NotBlank
        @Schema(example = "AB12CD34")
        String codigo) {
}
