package com.chezticket.shared.domain;

/**
 * Raiz de todas as exceções de regra de negócio do domínio.
 * Vive no núcleo (sem dependência de framework); os adaptadores é que
 * decidem como traduzi-la para o mundo externo (ex.: um status HTTP).
 */
public abstract class DominioException extends RuntimeException {

    protected DominioException(String mensagem) {
        super(mensagem);
    }
}
