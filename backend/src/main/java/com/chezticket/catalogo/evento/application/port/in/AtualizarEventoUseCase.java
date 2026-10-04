package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.Evento;

public interface AtualizarEventoUseCase {

    Evento atualizar(Long eventoId, DadosEvento dados);
}
