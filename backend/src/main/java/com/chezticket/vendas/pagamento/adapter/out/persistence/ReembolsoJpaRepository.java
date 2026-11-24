package com.chezticket.vendas.pagamento.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface ReembolsoJpaRepository extends JpaRepository<ReembolsoJpaEntity, Long> {

    List<ReembolsoJpaEntity> findAllByPagamentoId(Long pagamentoId);
}
