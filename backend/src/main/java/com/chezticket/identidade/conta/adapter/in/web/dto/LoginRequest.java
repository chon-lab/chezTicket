package com.chezticket.identidade.conta.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Schema(example = "maria@exemplo.com")
        String email,

        @NotBlank
        String senha) {
}
