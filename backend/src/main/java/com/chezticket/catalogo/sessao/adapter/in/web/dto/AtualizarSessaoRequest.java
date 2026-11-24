package com.chezticket.catalogo.sessao.adapter.in.web.dto;

import com.chezticket.catalogo.sessao.application.port.in.DadosSessao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AtualizarSessaoRequest(
        @NotNull
        Long localId,

        @Size(max = 255)
        String endereco,

        @NotNull
        LocalDateTime inicio,

        @NotNull
        LocalDateTime fim) {

    public DadosSessao paraComando() {
        return new DadosSessao(localId, endereco, inicio, fim);
    }
}
