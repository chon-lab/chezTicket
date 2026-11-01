package com.chezticket.catalogo.ingresso.application.port.out;

/**
 * Porta de saída: o módulo de tipo de ingresso precisa confirmar que a sessão informada existe.
 * A implementação faz a ponte com o módulo de catálogo/sessão.
 */
public interface VerificarSessaoPort {

    boolean sessaoExiste(Long sessaoId);
}
