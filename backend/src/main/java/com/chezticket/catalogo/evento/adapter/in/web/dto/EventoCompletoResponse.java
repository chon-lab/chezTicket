package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.application.port.in.EventoCompletoCriado;
import com.chezticket.catalogo.ingresso.adapter.in.web.dto.TipoIngressoResponse;
import com.chezticket.catalogo.sessao.adapter.in.web.dto.SessaoResponse;
import java.util.List;

public record EventoCompletoResponse(
        EventoResponse evento,
        SessaoResponse sessao,
        List<TipoIngressoResponse> ingressos) {

    public static EventoCompletoResponse de(EventoCompletoCriado criado) {
        return new EventoCompletoResponse(
                EventoResponse.de(criado.evento()),
                SessaoResponse.de(criado.sessao()),
                criado.ingressos().stream().map(TipoIngressoResponse::de).toList());
    }
}
