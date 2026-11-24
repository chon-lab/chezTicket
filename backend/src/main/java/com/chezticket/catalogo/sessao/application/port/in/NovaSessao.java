package com.chezticket.catalogo.sessao.application.port.in;

import java.time.LocalDateTime;

/** Comando para agendar uma sessão nova. Informe {@code localId}, {@code endereco}, ou ambos. */
public record NovaSessao(
        Long eventoId,
        Long localId,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim) {
}
