package com.chezticket.catalogo.sessao.application.port.in;

import com.chezticket.catalogo.sessao.domain.StatusSessao;

/** Critérios opcionais para listar sessões. Campos nulos não filtram. */
public record FiltroSessoes(
        Long eventoId,
        StatusSessao status) {

    public static FiltroSessoes vazio() {
        return new FiltroSessoes(null, null);
    }
}
