package com.chezticket.identidade.conta.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Escreve a linha de {@code organizador} criada junto com a conta. Modelo simplificado: o
 * cadastro não coleta razão social/documento próprios, então eles recebem nome/CPF do usuário
 * como valor padrão (taxa_servico e dados_repasse ficam no padrão do banco).
 */
@Entity
@Table(name = "organizador")
class OrganizadorJpaEntity {

    @Id
    private Long usuarioId;

    @Column(name = "razao_social", nullable = false, length = 160)
    private String razaoSocial;

    @Column(nullable = false, length = 18)
    private String documento;

    protected OrganizadorJpaEntity() {
    }

    OrganizadorJpaEntity(Long usuarioId, String razaoSocial, String documento) {
        this.usuarioId = usuarioId;
        this.razaoSocial = razaoSocial;
        this.documento = documento;
    }
}
