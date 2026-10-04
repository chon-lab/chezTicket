package com.chezticket.catalogo.categoria.application.port.out;

import com.chezticket.catalogo.categoria.domain.Categoria;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída: o que a aplicação precisa de um repositório de categorias.
 * A implementação (adaptador) vive em {@code adapter.out.persistence} e usa JPA/MariaDB,
 * mas isso é invisível para o domínio e para a aplicação.
 */
public interface CategoriaRepositorio {

    Categoria salvar(Categoria categoria);

    Optional<Categoria> buscarPorId(Long id);

    boolean existePorId(Long id);

    List<Categoria> listarTodas();

    List<Categoria> listarAtivas();

    boolean existePorNome(String nome);

    boolean existePorNomeExcetoId(String nome, Long id);

    void remover(Long id);
}
