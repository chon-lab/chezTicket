package com.chezticket.vendas.pagamento.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface PagamentoJpaRepository extends JpaRepository<PagamentoJpaEntity, Long> {

    Optional<PagamentoJpaEntity> findByPedidoId(Long pedidoId);
}
