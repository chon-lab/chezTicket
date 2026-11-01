package com.chezticket.vendas.reserva.adapter.out.persistence;

import com.chezticket.vendas.reserva.domain.ItemReserva;
import com.chezticket.vendas.reserva.domain.Reserva;

final class ReservaPersistenceMapper {

    private ReservaPersistenceMapper() {
    }

    static ReservaJpaEntity paraEntidade(Reserva reserva) {
        ReservaJpaEntity entidade = new ReservaJpaEntity(
                reserva.id(),
                reserva.participanteId(),
                reserva.expiraEm(),
                reserva.status(),
                reserva.criadaEm());
        for (ItemReserva item : reserva.itens()) {
            entidade.adicionarItem(item.tipoIngressoId(), item.quantidade());
        }
        return entidade;
    }

    static Reserva paraDominio(ReservaJpaEntity entidade) {
        return Reserva.reconstituir(
                entidade.getId(),
                entidade.getParticipanteId(),
                entidade.getItens().stream()
                        .map(item -> new ItemReserva(item.getTipoIngressoId(), item.getQuantidade()))
                        .toList(),
                entidade.getExpiraEm(),
                entidade.getStatus(),
                entidade.getCriadaEm());
    }
}
