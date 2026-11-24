package com.chezticket.catalogo.eventocompleto.application;

import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;

public record EventoCompletoResultado(Evento evento, Sessao sessao, List<TipoIngresso> ingressos) {
}
