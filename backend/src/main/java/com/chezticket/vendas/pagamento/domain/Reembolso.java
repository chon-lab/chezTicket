package com.chezticket.vendas.pagamento.domain;

import com.chezticket.vendas.pagamento.domain.exception.PagamentoInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Reembolso de um {@code Pagamento}, feito via Stripe. Modelo de domínio puro. */
public class Reembolso {

    private Long id;
    private final Long pagamentoId;
    private final MotivoReembolso motivo;
    private final TipoReembolso tipo;
    private final BigDecimal valor;
    private StatusReembolso status;
    private final LocalDateTime solicitadoEm;
    private LocalDateTime processadoEm;

    private Reembolso(Long id, Long pagamentoId, MotivoReembolso motivo, TipoReembolso tipo, BigDecimal valor,
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

    /** Solicita um reembolso novo, ainda pendente de confirmação do gateway. */
    public static Reembolso solicitar(Long pagamentoId, MotivoReembolso motivo, TipoReembolso tipo,
            BigDecimal valor) {
        Objects.requireNonNull(pagamentoId, "pagamentoId");
        Objects.requireNonNull(motivo, "motivo");
        Objects.requireNonNull(tipo, "tipo");
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagamentoInvalidoException("O valor do reembolso deve ser maior que zero.");
        }
        return new Reembolso(null, pagamentoId, motivo, tipo, valor, StatusReembolso.PENDENTE,
                LocalDateTime.now(), null);
    }

    /** Reconstrói um reembolso já existente. Usado pelo adaptador de persistência. */
    public static Reembolso reconstituir(Long id, Long pagamentoId, MotivoReembolso motivo, TipoReembolso tipo,
            BigDecimal valor, StatusReembolso status, LocalDateTime solicitadoEm, LocalDateTime processadoEm) {
        return new Reembolso(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(pagamentoId, "pagamentoId"),
                motivo,
                tipo,
                valor,
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(solicitadoEm, "solicitadoEm"),
                processadoEm);
    }

    public void processar() {
        this.status = StatusReembolso.PROCESSADO;
        this.processadoEm = LocalDateTime.now();
    }

    public void negar() {
        this.status = StatusReembolso.NEGADO;
        this.processadoEm = LocalDateTime.now();
    }

    public Long id() {
        return id;
    }

    public Long pagamentoId() {
        return pagamentoId;
    }

    public MotivoReembolso motivo() {
        return motivo;
    }

    public TipoReembolso tipo() {
        return tipo;
    }

    public BigDecimal valor() {
        return valor;
    }

    public StatusReembolso status() {
        return status;
    }

    public LocalDateTime solicitadoEm() {
        return solicitadoEm;
    }

    public LocalDateTime processadoEm() {
        return processadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reembolso outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
