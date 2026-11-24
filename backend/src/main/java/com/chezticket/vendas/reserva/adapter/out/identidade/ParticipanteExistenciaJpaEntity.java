package com.chezticket.vendas.reserva.adapter.out.identidade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Leitura mínima da tabela {@code participante} — só o necessário para checar existência.
 * O cadastro completo de participantes vive em {@code identidade.conta}, que tem sua própria
 * entidade equivalente; o nome aqui é propositalmente diferente para não colidir com ela (Spring
 * Data registra repositórios pelo nome simples da interface, e o Hibernate faz o mesmo com o
 * nome da entidade — dois tipos com nomes iguais, mesmo em pacotes diferentes, colidem).
 */
@Entity(name = "ParticipanteExistencia")
@Table(name = "participante")
class ParticipanteExistenciaJpaEntity {

    @Id
    private Long usuarioId;

    protected ParticipanteExistenciaJpaEntity() {
    }
}
