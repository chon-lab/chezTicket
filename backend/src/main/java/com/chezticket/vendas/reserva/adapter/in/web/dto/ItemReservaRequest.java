package com.chezticket.vendas.reserva.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemReservaRequest(
        @NotNull
        @Schema(example = "1")
        Long tipoIngressoId,

        @Positive
        @Schema(example = "2")
        int quantidade) {
}
