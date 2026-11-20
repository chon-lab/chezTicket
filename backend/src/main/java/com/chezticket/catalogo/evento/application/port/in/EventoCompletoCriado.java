package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;

public record EventoCompletoCriado(
        Evento evento,
        Sessao sessao,
        List<TipoIngresso> ingressos) {
}
