package com.chezticket.vendas.cupom.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapeia a tabela {@code cupom} já existente (V1), usando só o que o modelo simplificado
 * precisa: {@code tipo} é sempre {@code PERCENTUAL}; {@code limite_usos}/{@code usos} ficam no
 * padrão do banco (não há {@code Pedido} para consumi-los ainda).
 */
@Entity
@Table(name = "cupom")
class CupomJpaEntity {

    private enum Tipo {
        PERCENTUAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id")
    private Long eventoId;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Tipo tipo = Tipo.PERCENTUAL;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    private LocalDate validade;

    protected CupomJpaEntity() {
    }

    CupomJpaEntity(Long id, Long eventoId, String codigo, int percentualDesconto, LocalDate validade) {
        this.id = id;
        this.eventoId = eventoId;
        this.codigo = codigo;
        this.valor = BigDecimal.valueOf(percentualDesconto);
        this.validade = validade;
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

    int getPercentualDesconto() {
        return valor.intValue();
    }

    LocalDate getValidade() {
        return validade;
    }
}
