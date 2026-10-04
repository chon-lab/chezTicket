package com.chezticket.catalogo.evento.application.port.out;

import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.domain.Evento;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de eventos. */
public interface EventoRepositorio {

    Evento salvar(Evento evento);

    Optional<Evento> buscarPorId(Long id);

    List<Evento> buscar(FiltroEventos filtro);

    void remover(Long id);
}
