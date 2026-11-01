package com.chezticket.catalogo.sessao.adapter.in.web.dto;

import com.chezticket.catalogo.sessao.application.port.in.DadosSessao;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AtualizarSessaoRequest(
        @NotNull
        Long localId,

        @NotNull
        LocalDateTime inicio,

        @NotNull
        LocalDateTime fim) {

    public DadosSessao paraComando() {
        return new DadosSessao(localId, inicio, fim);
    }
}
