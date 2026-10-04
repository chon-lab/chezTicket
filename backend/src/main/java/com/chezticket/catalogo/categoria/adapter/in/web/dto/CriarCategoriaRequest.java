package com.chezticket.catalogo.categoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarCategoriaRequest(
        @NotBlank(message = "Informe o nome da categoria.")
        @Size(max = 80, message = "O nome pode ter no máximo 80 caracteres.")
        String nome) {
}
