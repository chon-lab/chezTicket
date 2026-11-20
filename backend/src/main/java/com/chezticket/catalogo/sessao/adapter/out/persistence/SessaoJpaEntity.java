package com.chezticket.catalogo.sessao.adapter.out.persistence;

import com.chezticket.catalogo.sessao.domain.StatusSessao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
class SessaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "local_id")
    private Long localId;

    @Column(length = 500)
    private String endereco;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSessao status;

    protected SessaoJpaEntity() {
    }

    SessaoJpaEntity(Long id, Long eventoId, Long localId, String endereco,
            LocalDateTime inicio, LocalDateTime fim, StatusSessao status) {
        this.id = id;
        this.eventoId = eventoId;
        this.localId = localId;
        this.endereco = endereco;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
    }

    Long getId() {
        return id;
    }

    Long getEventoId() {
        return eventoId;
    }

    Long getLocalId() {
        return localId;
    }

    String getEndereco() {
        return endereco;
    }

    LocalDateTime getInicio() {
        return inicio;
    }

    LocalDateTime getFim() {
        return fim;
    }

    StatusSessao getStatus() {
        return status;
    }
}
