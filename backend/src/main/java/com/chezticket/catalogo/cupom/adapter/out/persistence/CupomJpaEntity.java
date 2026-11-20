package com.chezticket.catalogo.cupom.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupom")
class CupomJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id")
    private Long eventoId;

    @Column(nullable = false, length = 40, unique = true)
    private String codigo;

    @Column(nullable = false, length = 20)
    private String tipo = "PERCENTUAL";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "valido_ate")
    private LocalDateTime validoAte;

    @Column(name = "limite_usos", nullable = false)
    private int limiteUsos;

    @Column(nullable = false)
    private int usos;

    protected CupomJpaEntity() {
    }

    CupomJpaEntity(Long id, Long eventoId, String codigo, BigDecimal valor, LocalDateTime validoAte) {
        this.id = id;
        this.eventoId = eventoId;
        this.codigo = codigo;
        this.valor = valor;
        this.validoAte = validoAte;
    }

    Long getId() {
        return id;
    }

    Long getEventoId() {
        return eventoId;
    }

    String getCodigo() {
        return codigo;
    }

    String getTipo() {
        return tipo;
    }

    BigDecimal getValor() {
        return valor;
    }

    LocalDateTime getValidoAte() {
        return validoAte;
    }

    int getLimiteUsos() {
        return limiteUsos;
    }

    int getUsos() {
        return usos;
    }
}
