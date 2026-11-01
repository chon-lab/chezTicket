package com.chezticket.catalogo.sessao.application.port.out;

/**
 * Porta de saída: o módulo de sessão precisa confirmar que o local informado existe.
 * A implementação faz a ponte com o módulo de catálogo/local.
 */
public interface VerificarLocalPort {

    boolean localExiste(Long localId);
}
