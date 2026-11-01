package com.chezticket.catalogo.local.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface LocalJpaRepository extends JpaRepository<LocalJpaEntity, Long> {

    List<LocalJpaEntity> findAllByOrderByNomeAsc();
}
