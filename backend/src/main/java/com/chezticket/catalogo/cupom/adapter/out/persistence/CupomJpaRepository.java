package com.chezticket.catalogo.cupom.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface CupomJpaRepository extends JpaRepository<CupomJpaEntity, Long> {

    boolean existsByCodigo(String codigo);

    Optional<CupomJpaEntity> findByCodigoAndTipo(String codigo, String tipo);
}
