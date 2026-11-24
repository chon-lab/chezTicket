package com.chezticket.vendas.pedido.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
class ItemPedidoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoJpaEntity pedido;

    @Column(name = "tipo_ingresso_id", nullable = false)
    private Long tipoIngressoId;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    protected ItemPedidoJpaEntity() {
    }

    ItemPedidoJpaEntity(PedidoJpaEntity pedido, Long tipoIngressoId, int quantidade, BigDecimal precoUnitario) {
        this.pedido = pedido;
        this.tipoIngressoId = tipoIngressoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    Long getTipoIngressoId() {
        return tipoIngressoId;
    }

    int getQuantidade() {
        return quantidade;
    }

    BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}
