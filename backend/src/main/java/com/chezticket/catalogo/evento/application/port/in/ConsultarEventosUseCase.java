package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.Evento;
import java.util.List;

public interface ConsultarEventosUseCase {

    Evento buscarPorId(Long eventoId);

    List<Evento> listar(FiltroEventos filtro);
}
