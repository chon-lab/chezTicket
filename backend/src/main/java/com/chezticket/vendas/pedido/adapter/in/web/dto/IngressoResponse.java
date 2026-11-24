package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.ingresso.domain.Ingresso;
import java.time.LocalDateTime;

public record IngressoResponse(
        Long id,
        Long pedidoId,
        Long tipoIngressoId,
        String codigoQr,
        String status,
        String titular,
        LocalDateTime dataEmissao) {

    public static IngressoResponse de(Ingresso i) {
        return new IngressoResponse(i.id(), i.pedidoId(), i.tipoIngressoId(), i.codigoQr(), i.status().name(),
                i.titular(), i.dataEmissao());
    }
}
