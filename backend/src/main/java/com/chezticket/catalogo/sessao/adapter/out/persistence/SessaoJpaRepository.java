package com.chezticket.catalogo.sessao.adapter.out.persistence;

import com.chezticket.catalogo.sessao.domain.StatusSessao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SessaoJpaRepository extends JpaRepository<SessaoJpaEntity, Long> {

    @Query("""
            select s from SessaoJpaEntity s
            where (:eventoId is null or s.eventoId = :eventoId)
              and (:status is null or s.status = :status)
            order by s.inicio asc
            """)
    List<SessaoJpaEntity> buscar(
            @Param("eventoId") Long eventoId,
            @Param("status") StatusSessao status);
}
