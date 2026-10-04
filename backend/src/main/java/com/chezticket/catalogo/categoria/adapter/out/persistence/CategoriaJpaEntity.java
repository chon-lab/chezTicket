package com.chezticket.catalogo.categoria.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Representação de {@code Categoria} para o JPA. É um detalhe do adaptador de
 * persistência: package-private, sem regra de negócio e sem vazar para fora daqui.
 */
@Entity
@Table(name = "categoria")
class CategoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false)
    private boolean ativa;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    protected CategoriaJpaEntity() {
        // exigido pelo JPA
    }

    CategoriaJpaEntity(Long id, String nome, String slug, boolean ativa, LocalDateTime criadaEm) {
        this.id = id;
        this.nome = nome;
        this.slug = slug;
        this.ativa = ativa;
        this.criadaEm = criadaEm;
    }

    Long getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    String getSlug() {
        return slug;
    }

    boolean isAtiva() {
        return ativa;
    }

    LocalDateTime getCriadaEm() {
        return criadaEm;
    }
}
