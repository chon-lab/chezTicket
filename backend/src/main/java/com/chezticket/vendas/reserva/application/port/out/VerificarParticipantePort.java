package com.chezticket.vendas.reserva.application.port.out;

/**
 * Porta de saída: o módulo de reserva precisa confirmar que o participante informado existe.
 * A implementação faz a ponte com a tabela {@code participante} (fora do escopo deste módulo).
 */
public interface VerificarParticipantePort {

    boolean participanteExiste(Long participanteId);
}
