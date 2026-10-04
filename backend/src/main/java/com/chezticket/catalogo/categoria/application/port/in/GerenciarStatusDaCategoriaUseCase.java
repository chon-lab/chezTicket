package com.chezticket.catalogo.categoria.application.port.in;

import com.chezticket.catalogo.categoria.domain.Categoria;

/** Ativar/desativar uma categoria (não aparece mais nas buscas padrão quando inativa). */
public interface GerenciarStatusDaCategoriaUseCase {

    Categoria ativar(Long id);

    Categoria desativar(Long id);
}
