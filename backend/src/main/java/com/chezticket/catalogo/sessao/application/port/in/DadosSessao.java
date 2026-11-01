package com.chezticket.catalogo.sessao.application.port.in;

import java.time.LocalDateTime;

/** Comando para reagendar (local/horário) uma sessão existente. */
public record DadosSessao(
        Long localId,
        LocalDateTime inicio,
        LocalDateTime fim) {
}
