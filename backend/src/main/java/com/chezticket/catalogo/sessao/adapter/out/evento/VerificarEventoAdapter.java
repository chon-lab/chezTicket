package com.chezticket.catalogo.sessao.adapter.out.evento;

import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.sessao.application.port.out.VerificarEventoPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de sessão ao módulo de evento,
 * reusando a porta pública {@link EventoRepositorio}. Único ponto de
 * acoplamento entre os dois módulos.
 */
@Component
class VerificarEventoAdapter implements VerificarEventoPort {

    private final EventoRepositorio eventos;

    VerificarEventoAdapter(EventoRepositorio eventos) {
        this.eventos = eventos;
    }

    @Override
    public boolean eventoExiste(Long eventoId) {
        return eventoId != null && eventos.buscarPorId(eventoId).isPresent();
    }
}
