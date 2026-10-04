package com.chezticket.catalogo.categoria.adapter.in.web.dto;

import com.chezticket.catalogo.categoria.domain.Categoria;
import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        String nome,
        String slug,
        boolean ativa,
        LocalDateTime criadaEm) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(
                categoria.id(),
                categoria.nome(),
                categoria.slug(),
                categoria.ativa(),
                categoria.criadaEm());
    }
}
