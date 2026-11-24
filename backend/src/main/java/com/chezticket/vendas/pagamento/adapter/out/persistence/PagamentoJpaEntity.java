package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.domain.MetodoPagamento;
import com.chezticket.vendas.pagamento.domain.StatusPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
class PagamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(nullable = false, length = 40)
    private String gateway;

    @Column(name = "id_transacao", length = 120)
    private String idTransacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Column(nullable = false)
    private int parcelas;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected PagamentoJpaEntity() {
    }

    PagamentoJpaEntity(Long id, Long pedidoId, String gateway, String idTransacao, MetodoPagamento metodo,
            int parcelas, BigDecimal valor, StatusPagamento status, LocalDateTime criadoEm) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.gateway = gateway;
        this.idTransacao = idTransacao;
        this.metodo = metodo;
        this.parcelas = parcelas;
        this.valor = valor;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    Long getId() {
        return id;
    }

    Long getPedidoId() {
        return pedidoId;
    }

    String getGateway() {
        return gateway;
    }

    String getIdTransacao() {
        return idTransacao;
    }

    MetodoPagamento getMetodo() {
        return metodo;
    }

    int getParcelas() {
        return parcelas;
    }

    BigDecimal getValor() {
        return valor;
    }

    StatusPagamento getStatus() {
        return status;
    }

    LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
