package com.chezticket.catalogo.sessao.application.port.in;

import java.time.LocalDateTime;

/** Comando para reagendar (local/endereço/horário) uma sessão existente. */
public record DadosSessao(
        Long localId,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim) {
}
