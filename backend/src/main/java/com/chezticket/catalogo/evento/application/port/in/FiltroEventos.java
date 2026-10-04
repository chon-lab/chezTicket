package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.StatusEvento;

/** Critérios opcionais para listar eventos. Campos nulos não filtram. */
public record FiltroEventos(
        StatusEvento status,
        Long categoriaId,
        Long organizadorId) {

    public static FiltroEventos vazio() {
        return new FiltroEventos(null, null, null);
    }
}
