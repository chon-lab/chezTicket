package com.chezticket.vendas.reserva.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_reserva")
class ItemReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaJpaEntity reserva;

    @Column(name = "tipo_ingresso_id", nullable = false)
    private Long tipoIngressoId;

    @Column(nullable = false)
    private int quantidade;

    protected ItemReservaJpaEntity() {
    }

    ItemReservaJpaEntity(ReservaJpaEntity reserva, Long tipoIngressoId, int quantidade) {
        this.reserva = reserva;
        this.tipoIngressoId = tipoIngressoId;
        this.quantidade = quantidade;
    }

    Long getTipoIngressoId() {
        return tipoIngressoId;
    }

    int getQuantidade() {
        return quantidade;
    }
}
