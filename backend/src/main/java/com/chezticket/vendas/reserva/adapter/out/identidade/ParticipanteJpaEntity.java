package com.chezticket.vendas.reserva.adapter.out.identidade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Leitura mínima da tabela {@code participante} — só o necessário para checar existência.
 * O cadastro completo de participantes (identidade/autenticação) vive fora do escopo deste módulo.
 */
@Entity
@Table(name = "participante")
class ParticipanteJpaEntity {

    @Id
    private Long usuarioId;

    protected ParticipanteJpaEntity() {
    }
}
