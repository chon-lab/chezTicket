package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface TipoIngressoJpaRepository extends JpaRepository<TipoIngressoJpaEntity, Long> {

    List<TipoIngressoJpaEntity> findAllBySessaoIdOrderByNomeAsc(Long sessaoId);
}
