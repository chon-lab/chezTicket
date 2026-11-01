package com.chezticket.catalogo.local.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "local")
class LocalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nome;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(nullable = false, length = 120)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(name = "capacidade_total", nullable = false)
    private int capacidadeTotal;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected LocalJpaEntity() {
    }

    LocalJpaEntity(Long id, String nome, String endereco, String cidade, String uf, int capacidadeTotal,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.uf = uf;
        this.capacidadeTotal = capacidadeTotal;
        this.criadoEm = criadoEm;
    }

    Long getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    String getEndereco() {
        return endereco;
    }

    String getCidade() {
        return cidade;
    }

    String getUf() {
        return uf;
    }

    int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
