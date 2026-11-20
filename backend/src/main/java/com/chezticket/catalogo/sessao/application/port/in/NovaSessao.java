package com.chezticket.catalogo.sessao.application.port.in;

import java.time.LocalDateTime;

/** Comando para agendar uma sessão nova. */
public record NovaSessao(
        Long eventoId,
        Long localId,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim) {

    public NovaSessao(Long eventoId, Long localId, LocalDateTime inicio, LocalDateTime fim) {
        this(eventoId, localId, null, inicio, fim);
    }

    public static NovaSessao comEndereco(
            Long eventoId, String endereco, LocalDateTime inicio, LocalDateTime fim) {
        return new NovaSessao(eventoId, null, endereco, inicio, fim);
    }
}
