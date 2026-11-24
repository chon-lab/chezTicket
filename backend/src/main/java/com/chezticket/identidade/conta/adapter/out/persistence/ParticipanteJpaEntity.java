package com.chezticket.identidade.conta.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Escreve a linha de {@code participante} criada junto com a conta. Existe também uma leitura
 * mínima equivalente em {@code vendas.reserva} (só para checar existência) — duplicada de
 * propósito: cada módulo só enxerga da tabela o que precisa, sem acoplamento entre eles.
 */
@Entity
@Table(name = "participante")
class ParticipanteJpaEntity {

    @Id
    private Long usuarioId;

    @Column(length = 14)
    private String cpf;

    protected ParticipanteJpaEntity() {
    }

    ParticipanteJpaEntity(Long usuarioId, String cpf) {
        this.usuarioId = usuarioId;
        this.cpf = cpf;
    }

    Long getUsuarioId() {
        return usuarioId;
    }

    String getCpf() {
        return cpf;
    }
}
