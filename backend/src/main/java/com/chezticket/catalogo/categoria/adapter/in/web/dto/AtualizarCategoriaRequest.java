package com.chezticket.catalogo.categoria.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarCategoriaRequest(
        @NotBlank(message = "Informe o nome da categoria.")
        @Size(max = 80, message = "O nome pode ter no máximo 80 caracteres.")
        @Schema(example = "Shows e Festivais")
        String nome) {
}
