package com.chezticket.vendas.pedido.application.port.out;

/** Porta de saída: liga o módulo de pedido ao módulo de conta, só para obter o e-mail de contato. */
public interface ConsultarEmailDoParticipantePort {

    String buscarEmail(Long participanteId);
}
