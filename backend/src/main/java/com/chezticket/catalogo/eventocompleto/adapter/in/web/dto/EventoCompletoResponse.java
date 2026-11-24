package com.chezticket.catalogo.eventocompleto.adapter.in.web.dto;

import com.chezticket.catalogo.eventocompleto.application.EventoCompletoResultado;
import com.chezticket.catalogo.evento.adapter.in.web.dto.EventoResponse;
import com.chezticket.catalogo.ingresso.adapter.in.web.dto.TipoIngressoResponse;
import com.chezticket.catalogo.sessao.adapter.in.web.dto.SessaoResponse;
import com.chezticket.shared.web.UploadUrlResolver;
import java.util.List;

public record EventoCompletoResponse(EventoResponse evento, SessaoResponse sessao, List<TipoIngressoResponse> ingressos) {

    public static EventoCompletoResponse de(EventoCompletoResultado resultado, UploadUrlResolver uploads) {
        return new EventoCompletoResponse(
                EventoResponse.de(resultado.evento(), uploads),
                SessaoResponse.de(resultado.sessao()),
                resultado.ingressos().stream().map(TipoIngressoResponse::de).toList());
    }
}
