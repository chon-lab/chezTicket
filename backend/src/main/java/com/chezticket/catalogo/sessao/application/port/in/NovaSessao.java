package com.chezticket.catalogo.sessao.application.port.in;

import java.time.LocalDateTime;

/** Comando para agendar uma sessão nova. */
public record NovaSessao(
        Long eventoId,
        Long localId,
        LocalDateTime inicio,
        LocalDateTime fim) {
}
