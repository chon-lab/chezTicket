package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.domain.StatusEvento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, Long> {

    @Query("""
            select e from EventoJpaEntity e
            where (:status is null or e.status = :status)
              and (:categoriaId is null or e.categoriaId = :categoriaId)
              and (:organizadorId is null or e.organizadorId = :organizadorId)
            order by e.criadoEm desc
            """)
    List<EventoJpaEntity> buscar(
            @Param("status") StatusEvento status,
            @Param("categoriaId") Long categoriaId,
            @Param("organizadorId") Long organizadorId);
}
