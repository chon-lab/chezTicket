package com.chezticket.vendas.ingresso.adapter.out.persistence;

import com.chezticket.vendas.ingresso.domain.StatusIngresso;
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
@Table(name = "ingresso")
class IngressoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "tipo_ingresso_id", nullable = false)
    private Long tipoIngressoId;

    @Column(name = "codigo_qr", nullable = false, unique = true, length = 64)
    private String codigoQr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusIngresso status;

    @Column(length = 160)
    private String titular;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    protected IngressoJpaEntity() {
    }

    IngressoJpaEntity(Long id, Long pedidoId, Long tipoIngressoId, String codigoQr, StatusIngresso status,
            String titular, LocalDateTime dataEmissao) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.tipoIngressoId = tipoIngressoId;
        this.codigoQr = codigoQr;
        this.status = status;
        this.titular = titular;
        this.dataEmissao = dataEmissao;
    }

    Long getId() {
        return id;
    }

    Long getPedidoId() {
        return pedidoId;
    }

    Long getTipoIngressoId() {
        return tipoIngressoId;
    }

    String getCodigoQr() {
        return codigoQr;
    }

    StatusIngresso getStatus() {
        return status;
    }

    String getTitular() {
        return titular;
    }

    LocalDateTime getDataEmissao() {
        return dataEmissao;
    }
}
