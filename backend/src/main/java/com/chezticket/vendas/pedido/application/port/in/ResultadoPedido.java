package com.chezticket.vendas.pedido.application.port.in;

import com.chezticket.vendas.ingresso.domain.Ingresso;
import com.chezticket.vendas.pedido.domain.Pedido;
import java.util.List;

/**
 * @param checkoutUrl URL de pagamento Stripe para onde o comprador deve ser redirecionado;
 *                     {@code null} quando o pedido já nasce quitado (ingressos gratuitos) e
 *                     não passa pelo gateway
 * @param ingressos    ingressos já emitidos; vazio enquanto o pagamento está pendente
 */
public record ResultadoPedido(Pedido pedido, String checkoutUrl, List<Ingresso> ingressos) {
}
