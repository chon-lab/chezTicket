package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.evento.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class EventoPersistenceAdapter implements EventoRepositorio {

    private final EventoJpaRepository jpa;

    EventoPersistenceAdapter(EventoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Evento salvar(Evento evento) {
        return EventoPersistenceMapper.paraDominio(
                jpa.save(EventoPersistenceMapper.paraEntidade(evento)));
    }

    @Override
    public Optional<Evento> buscarPorId(Long id) {
        return jpa.findById(id).map(EventoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Evento> buscar(FiltroEventos filtro) {
        return jpa.buscar(filtro.status(), filtro.categoriaId(), filtro.organizadorId()).stream()
                .map(EventoPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }
}
