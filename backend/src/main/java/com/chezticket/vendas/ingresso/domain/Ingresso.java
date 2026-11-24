package com.chezticket.vendas.ingresso.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Ingresso emitido após a confirmação de pagamento de um {@code Pedido}. Modelo de domínio puro.
 *
 * <p>Check-in (validação na entrada do evento) não está no escopo desta fase — este modelo cobre
 * só a emissão, que é o que a Fase 5 do cronograma pede ("emissão de ingresso/QR Code após
 * confirmação").
 */
public class Ingresso {

    private Long id;
    private final Long pedidoId;
    private final Long tipoIngressoId;
    private final String codigoQr;
    private StatusIngresso status;
    private final String titular;
    private final LocalDateTime dataEmissao;

    private Ingresso(Long id, Long pedidoId, Long tipoIngressoId, String codigoQr, StatusIngresso status,
            String titular, LocalDateTime dataEmissao) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.tipoIngressoId = tipoIngressoId;
        this.codigoQr = codigoQr;
        this.status = status;
        this.titular = titular;
        this.dataEmissao = dataEmissao;
    }

    /** Emite um ingresso novo, com um código de QR único. */
    public static Ingresso emitir(Long pedidoId, Long tipoIngressoId, String titular) {
        Objects.requireNonNull(pedidoId, "pedidoId");
        Objects.requireNonNull(tipoIngressoId, "tipoIngressoId");
        return new Ingresso(null, pedidoId, tipoIngressoId, UUID.randomUUID().toString(), StatusIngresso.EMITIDO,
                titular, LocalDateTime.now());
    }

    /** Reconstrói um ingresso já existente. Usado pelo adaptador de persistência. */
    public static Ingresso reconstituir(Long id, Long pedidoId, Long tipoIngressoId, String codigoQr,
            StatusIngresso status, String titular, LocalDateTime dataEmissao) {
        return new Ingresso(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(pedidoId, "pedidoId"),
                Objects.requireNonNull(tipoIngressoId, "tipoIngressoId"),
                codigoQr,
                Objects.requireNonNull(status, "status"),
                titular,
                dataEmissao);
    }

    public void cancelar() {
        this.status = StatusIngresso.CANCELADO;
    }

    public Long id() {
        return id;
    }

    public Long pedidoId() {
        return pedidoId;
    }

    public Long tipoIngressoId() {
        return tipoIngressoId;
    }

    public String codigoQr() {
        return codigoQr;
    }

    public StatusIngresso status() {
        return status;
    }

    public String titular() {
        return titular;
    }

    public LocalDateTime dataEmissao() {
        return dataEmissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ingresso outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
