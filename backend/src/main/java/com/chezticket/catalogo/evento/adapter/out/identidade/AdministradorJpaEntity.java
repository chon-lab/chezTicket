package com.chezticket.catalogo.evento.adapter.out.identidade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Leitura mínima da tabela {@code administrador} — só o necessário para checar papel.
 * O cadastro completo de administradores vive fora do escopo deste módulo.
 */
@Entity
@Table(name = "administrador")
class AdministradorJpaEntity {

    @Id
    private Long usuarioId;

    protected AdministradorJpaEntity() {
    }
}
