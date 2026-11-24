package com.chezticket.vendas.pedido.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Long> {

    List<PedidoJpaEntity> findAllByParticipanteIdOrderByCriadoEmDesc(Long participanteId);
}
