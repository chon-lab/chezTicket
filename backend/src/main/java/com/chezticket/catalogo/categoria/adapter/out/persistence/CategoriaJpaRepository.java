package com.chezticket.catalogo.categoria.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data usado internamente pelo {@link CategoriaPersistenceAdapter}. */
interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    List<CategoriaJpaEntity> findAllByOrderByNomeAsc();

    List<CategoriaJpaEntity> findAllByAtivaTrueOrderByNomeAsc();
}
