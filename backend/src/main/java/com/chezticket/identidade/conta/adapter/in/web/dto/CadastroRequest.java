package com.chezticket.identidade.conta.adapter.in.web.dto;

import com.chezticket.identidade.conta.application.port.in.NovoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
        @NotBlank
        @Schema(example = "Maria Oliveira")
        String nome,

        @NotBlank
        @Schema(example = "maria@exemplo.com")
        String email,

        @NotBlank
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres.")
        String senha,

        @NotBlank
        @Schema(description = "Só dígitos ou com máscara — ambos são aceitos", example = "123.456.789-00")
        String cpf) {

    public NovoUsuario paraComando() {
        return new NovoUsuario(nome, email, senha, cpf);
    }
}
