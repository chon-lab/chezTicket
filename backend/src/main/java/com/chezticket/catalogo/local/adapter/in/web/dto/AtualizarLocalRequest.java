package com.chezticket.catalogo.local.adapter.in.web.dto;

import com.chezticket.catalogo.local.application.port.in.DadosLocal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AtualizarLocalRequest(
        @NotBlank
        @Size(max = 160)
        String nome,

        @NotBlank
        @Size(max = 255)
        String endereco,

        @NotBlank
        @Size(max = 120)
        String cidade,

        @NotBlank
        @Size(min = 2, max = 2)
        String uf,

        @Positive
        int capacidadeTotal) {

    public DadosLocal paraComando() {
        return new DadosLocal(nome, endereco, cidade, uf, capacidadeTotal);
    }
}
