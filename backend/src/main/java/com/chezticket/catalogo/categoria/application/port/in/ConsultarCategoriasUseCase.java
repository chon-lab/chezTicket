package com.chezticket.catalogo.categoria.application.port.in;

import com.chezticket.catalogo.categoria.domain.Categoria;
import java.util.List;

/** Porta de entrada: consultar (uma ou várias) categorias do catálogo. */
public interface ConsultarCategoriasUseCase {

    Categoria buscarPorId(Long id);

    List<Categoria> listar(boolean apenasAtivas);
}
