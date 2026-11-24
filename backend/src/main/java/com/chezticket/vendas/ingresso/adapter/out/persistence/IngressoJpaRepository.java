package com.chezticket.vendas.ingresso.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface IngressoJpaRepository extends JpaRepository<IngressoJpaEntity, Long> {

    List<IngressoJpaEntity> findAllByPedidoId(Long pedidoId);
}
