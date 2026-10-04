package com.chezticket.catalogo.evento.adapter.out.catalogo;

import com.chezticket.catalogo.categoria.application.port.out.CategoriaRepositorio;
import com.chezticket.catalogo.evento.application.port.out.VerificarCategoriaPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de eventos ao módulo de categoria,
 * reusando a porta pública {@link CategoriaRepositorio}. É o único ponto de
 * acoplamento entre os dois módulos e fica isolado aqui, fora do núcleo.
 */
@Component
class CategoriaVerificacaoAdapter implements VerificarCategoriaPort {

    private final CategoriaRepositorio categorias;

    CategoriaVerificacaoAdapter(CategoriaRepositorio categorias) {
        this.categorias = categorias;
    }

    @Override
    public boolean categoriaExiste(Long categoriaId) {
        return categorias.existePorId(categoriaId);
    }
}
