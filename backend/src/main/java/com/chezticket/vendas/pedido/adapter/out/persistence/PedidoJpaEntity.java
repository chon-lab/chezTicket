package com.chezticket.vendas.pedido.adapter.out.persistence;

import com.chezticket.vendas.pedido.domain.StatusPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
class PedidoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participante_id", nullable = false)
    private Long participanteId;

    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "cupom_id")
    private Long cupomId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "valor_taxa", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTaxa;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatusPedido status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedidoJpaEntity> itens = new ArrayList<>();

    protected PedidoJpaEntity() {
    }

    PedidoJpaEntity(Long id, Long participanteId, Long reservaId, Long cupomId, BigDecimal subtotal,
            BigDecimal valorTaxa, BigDecimal valorTotal, StatusPedido status, LocalDateTime criadoEm,
            LocalDateTime atualizadoEm) {
        this.id = id;
        this.participanteId = participanteId;
        this.reservaId = reservaId;
        this.cupomId = cupomId;
        this.subtotal = subtotal;
        this.valorTaxa = valorTaxa;
        this.valorTotal = valorTotal;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    void adicionarItem(Long tipoIngressoId, int quantidade, BigDecimal precoUnitario) {
        itens.add(new ItemPedidoJpaEntity(this, tipoIngressoId, quantidade, precoUnitario));
    }

    Long getId() {
        return id;
    }

    Long getParticipanteId() {
        return participanteId;
    }

    Long getReservaId() {
        return reservaId;
    }

    Long getCupomId() {
        return cupomId;
    }

    BigDecimal getSubtotal() {
        return subtotal;
    }

    BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    BigDecimal getValorTotal() {
        return valorTotal;
    }

    StatusPedido getStatus() {
        return status;
    }

    LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    List<ItemPedidoJpaEntity> getItens() {
        return itens;
    }
}
