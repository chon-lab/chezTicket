package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.pedido.application.port.in.ResultadoPedido;
import java.util.List;

/**
 * @param checkoutUrl URL de pagamento Stripe; {@code null} quando o pedido já nasceu quitado
 *                     (ingressos gratuitos) e os ingressos já vêm emitidos abaixo
 */
public record ResultadoPedidoResponse(PedidoResponse pedido, String checkoutUrl, List<IngressoResponse> ingressos) {

    public static ResultadoPedidoResponse de(ResultadoPedido r) {
        return new ResultadoPedidoResponse(
                PedidoResponse.de(r.pedido()),
                r.checkoutUrl(),
                r.ingressos().stream().map(IngressoResponse::de).toList());
    }
}
