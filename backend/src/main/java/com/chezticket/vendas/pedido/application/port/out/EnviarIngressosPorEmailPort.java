package com.chezticket.vendas.pedido.application.port.out;

import java.util.List;

/** Porta de saída: manda o e-mail de confirmação da compra, com o QR Code de cada ingresso. */
public interface EnviarIngressosPorEmailPort {

    void enviar(String destinatario, Long pedidoId, List<ItemEmailIngresso> ingressos);
}
