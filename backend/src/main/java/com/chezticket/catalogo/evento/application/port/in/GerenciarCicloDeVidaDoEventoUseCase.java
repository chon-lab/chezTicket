package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.Evento;

/** Transições de status do evento (Figura 7). */
public interface GerenciarCicloDeVidaDoEventoUseCase {

    Evento publicar(Long eventoId);

    Evento cancelar(Long eventoId);

    Evento encerrar(Long eventoId);
}
