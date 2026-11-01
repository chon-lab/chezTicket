package com.chezticket.vendas.reserva.adapter.out.persistence;

import com.chezticket.vendas.reserva.application.port.out.ReservaRepositorio;
import com.chezticket.vendas.reserva.domain.Reserva;
import com.chezticket.vendas.reserva.domain.StatusReserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class ReservaPersistenceAdapter implements ReservaRepositorio {

    private final ReservaJpaRepository jpa;

    ReservaPersistenceAdapter(ReservaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Reserva salvar(Reserva reserva) {
        return ReservaPersistenceMapper.paraDominio(
                jpa.save(ReservaPersistenceMapper.paraEntidade(reserva)));
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return jpa.findById(id).map(ReservaPersistenceMapper::paraDominio);
    }

    @Override
    public List<Reserva> listarPorParticipante(Long participanteId) {
        return jpa.findAllByParticipanteIdOrderByCriadaEmDesc(participanteId).stream()
                .map(ReservaPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Reserva> listarAtivasExpiradasAte(LocalDateTime referencia) {
        return jpa.findAllByStatusAndExpiraEmLessThanEqual(StatusReserva.ATIVA, referencia).stream()
                .map(ReservaPersistenceMapper::paraDominio)
                .toList();
    }
}
