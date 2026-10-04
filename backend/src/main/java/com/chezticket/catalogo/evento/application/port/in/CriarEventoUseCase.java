package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.Evento;

public interface CriarEventoUseCase {

    Evento criar(NovoEvento comando);
}
