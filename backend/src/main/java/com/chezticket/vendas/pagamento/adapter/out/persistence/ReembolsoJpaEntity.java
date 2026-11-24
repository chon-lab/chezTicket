package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.domain.MotivoReembolso;
import com.chezticket.vendas.pagamento.domain.StatusReembolso;
import com.chezticket.vendas.pagamento.domain.TipoReembolso;
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
@Table(name = "reembolso")
class ReembolsoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pagamento_id", nullable = false)
    private Long pagamentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MotivoReembolso motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoReembolso tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReembolso status;

    @Column(name = "solicitado_em", nullable = false)
    private LocalDateTime solicitadoEm;

    @Column(name = "processado_em")
    private LocalDateTime processadoEm;

    protected ReembolsoJpaEntity() {
    }

    ReembolsoJpaEntity(Long id, Long pagamentoId, MotivoReembolso motivo, TipoReembolso tipo, BigDecimal valor,
            StatusReembolso status, LocalDateTime solicitadoEm, LocalDateTime processadoEm) {
        this.id = id;
        this.pagamentoId = pagamentoId;
        this.motivo = motivo;
        this.tipo = tipo;
        this.valor = valor;
        this.status = status;
        this.solicitadoEm = solicitadoEm;
        this.processadoEm = processadoEm;
    }

    Long getId() {
        return id;
    }

    Long getPagamentoId() {
        return pagamentoId;
    }

    MotivoReembolso getMotivo() {
        return motivo;
    }

    TipoReembolso getTipo() {
        return tipo;
    }

    BigDecimal getValor() {
        return valor;
    }

    StatusReembolso getStatus() {
        return status;
    }

    LocalDateTime getSolicitadoEm() {
        return solicitadoEm;
    }

    LocalDateTime getProcessadoEm() {
        return processadoEm;
    }
}
