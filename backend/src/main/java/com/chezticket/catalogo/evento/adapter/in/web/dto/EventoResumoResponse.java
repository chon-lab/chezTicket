package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.domain.Evento;
import java.time.LocalDateTime;

/** Projeção enxuta usada nas listagens. */
public record EventoResumoResponse(
        Long id,
        String titulo,
        String status,
        Long categoriaId,
        Long organizadorId,
        LocalDateTime dataPublicacao) {

    public static EventoResumoResponse de(Evento e) {
        return new EventoResumoResponse(
                e.id(), e.titulo(), e.status().name(), e.categoriaId(), e.organizadorId(), e.dataPublicacao());
    }
}
