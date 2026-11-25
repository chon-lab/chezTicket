package com.chezticket.vendas.pedido.application.port.out;

/** Um ingresso emitido, pronto para entrar no e-mail de confirmação da compra. */
public record ItemEmailIngresso(String nomeDoTipo, String codigoQr) {
}
