package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TipoIngressoJpaRepository extends JpaRepository<TipoIngressoJpaEntity, Long> {

    List<TipoIngressoJpaEntity> findAllBySessaoIdOrderByNomeAsc(Long sessaoId);

    @Query(value = """
            SELECT s.evento_id
            FROM tipo_ingresso ti
            JOIN sessao s ON s.id = ti.sessao_id
            WHERE ti.id = :tipoIngressoId
            """, nativeQuery = true)
    Optional<Long> buscarEventoId(@Param("tipoIngressoId") Long tipoIngressoId);
}
