package com.chezticket.vendas.cupom.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface CupomJpaRepository extends JpaRepository<CupomJpaEntity, Long> {

    boolean existsByCodigo(String codigo);

    Optional<CupomJpaEntity> findByEventoIdAndCodigo(Long eventoId, String codigo);
}
