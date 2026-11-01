package com.chezticket.vendas.reserva.adapter.out.persistence;

import com.chezticket.vendas.reserva.domain.StatusReserva;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva")
class ReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participante_id", nullable = false)
    private Long participanteId;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemReservaJpaEntity> itens = new ArrayList<>();

    protected ReservaJpaEntity() {
    }

    ReservaJpaEntity(Long id, Long participanteId, LocalDateTime expiraEm, StatusReserva status,
            LocalDateTime criadaEm) {
        this.id = id;
        this.participanteId = participanteId;
        this.expiraEm = expiraEm;
        this.status = status;
        this.criadaEm = criadaEm;
    }

    void adicionarItem(Long tipoIngressoId, int quantidade) {
        itens.add(new ItemReservaJpaEntity(this, tipoIngressoId, quantidade));
    }

    Long getId() {
        return id;
    }

    Long getParticipanteId() {
        return participanteId;
    }

    LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    StatusReserva getStatus() {
        return status;
    }

    LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    List<ItemReservaJpaEntity> getItens() {
        return itens;
    }
}
