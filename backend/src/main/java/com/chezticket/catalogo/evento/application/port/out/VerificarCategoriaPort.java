package com.chezticket.catalogo.evento.application.port.out;

/**
 * Porta de saída: o módulo de eventos precisa confirmar que a categoria
 * informada existe. A implementação faz a ponte com o módulo de catálogo/categoria.
 */
public interface VerificarCategoriaPort {

    boolean categoriaExiste(Long categoriaId);
}
