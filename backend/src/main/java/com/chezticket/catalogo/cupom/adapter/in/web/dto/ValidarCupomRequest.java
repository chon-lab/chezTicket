package com.chezticket.catalogo.cupom.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ValidarCupomRequest(
        @NotNull Long eventoId,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9]{8}") String codigo) {
}
