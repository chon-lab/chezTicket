package com.chezticket.identidade.conta.adapter.out.persistence;

import com.chezticket.identidade.conta.domain.StatusUsuario;
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
@Table(name = "usuario")
class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusUsuario status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected UsuarioJpaEntity() {
    }

    UsuarioJpaEntity(Long id, String nome, String email, String senhaHash, StatusUsuario status,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    Long getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    String getEmail() {
        return email;
    }

    String getSenhaHash() {
        return senhaHash;
    }

    StatusUsuario getStatus() {
        return status;
    }

    LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
