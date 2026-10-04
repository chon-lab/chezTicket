package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import com.chezticket.catalogo.evento.domain.StatusEvento;
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
@Table(name = "evento")
class EventoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao_etaria", nullable = false, length = 20)
    private ClassificacaoEtaria classificacaoEtaria;

    @Column(name = "imagem_capa", length = 255)
    private String imagemCapa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEvento status;

    @Enumerated(EnumType.STRING)
    @Column(name = "politica_reembolso", nullable = false, length = 20)
    private PoliticaReembolso politicaReembolso;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    protected EventoJpaEntity() {
    }

    EventoJpaEntity(Long id, Long organizadorId, Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa, StatusEvento status,
            PoliticaReembolso politicaReembolso, LocalDateTime dataPublicacao, LocalDateTime criadoEm,
            LocalDateTime atualizadoEm) {
        this.id = id;
        this.organizadorId = organizadorId;
        this.categoriaId = categoriaId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.classificacaoEtaria = classificacaoEtaria;
        this.imagemCapa = imagemCapa;
        this.status = status;
        this.politicaReembolso = politicaReembolso;
        this.dataPublicacao = dataPublicacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    Long getId() {
        return id;
    }

    Long getOrganizadorId() {
        return organizadorId;
    }

    Long getCategoriaId() {
        return categoriaId;
    }

    String getTitulo() {
        return titulo;
    }

    String getDescricao() {
        return descricao;
    }

    ClassificacaoEtaria getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    String getImagemCapa() {
        return imagemCapa;
    }

    StatusEvento getStatus() {
        return status;
    }

    PoliticaReembolso getPoliticaReembolso() {
        return politicaReembolso;
    }

    LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
