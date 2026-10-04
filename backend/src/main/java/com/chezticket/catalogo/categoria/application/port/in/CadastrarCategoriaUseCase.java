package com.chezticket.catalogo.categoria.application.port.in;

import com.chezticket.catalogo.categoria.domain.Categoria;

/** Porta de entrada: cadastrar uma nova categoria no catálogo. */
public interface CadastrarCategoriaUseCase {

    Categoria cadastrar(String nome);
}
