package com.chezticket.vendas.reserva.adapter.out.persistence;

import com.chezticket.vendas.reserva.domain.StatusReserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface ReservaJpaRepository extends JpaRepository<ReservaJpaEntity, Long> {

    List<ReservaJpaEntity> findAllByParticipanteIdOrderByCriadaEmDesc(Long participanteId);

    List<ReservaJpaEntity> findAllByStatusAndExpiraEmLessThanEqual(StatusReserva status, LocalDateTime referencia);
}
