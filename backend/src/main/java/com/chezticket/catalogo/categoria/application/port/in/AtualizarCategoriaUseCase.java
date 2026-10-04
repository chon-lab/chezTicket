package com.chezticket.catalogo.categoria.application.port.in;

import com.chezticket.catalogo.categoria.domain.Categoria;

public interface AtualizarCategoriaUseCase {

    Categoria atualizar(Long id, String novoNome);
}
