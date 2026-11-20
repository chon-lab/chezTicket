package com.chezticket.catalogo.sessao.adapter.in.web.dto;

import com.chezticket.catalogo.sessao.domain.Sessao;
import java.time.LocalDateTime;

public record SessaoResponse(
        Long id,
        Long eventoId,
        Long localId,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim,
        String status) {

    public static SessaoResponse de(Sessao s) {
        return new SessaoResponse(
                s.id(), s.eventoId(), s.localId(), s.endereco(),
                s.inicio(), s.fim(), s.status().name());
    }
}
