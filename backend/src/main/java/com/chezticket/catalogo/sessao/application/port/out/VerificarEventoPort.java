package com.chezticket.catalogo.sessao.application.port.out;

/**
 * Porta de saída: o módulo de sessão precisa confirmar que o evento informado existe.
 * A implementação faz a ponte com o módulo de catálogo/evento.
 */
public interface VerificarEventoPort {

    boolean eventoExiste(Long eventoId);
}
