package com.chezticket.vendas.reserva.application.port.out;

/**
 * Porta de saída: o módulo de reserva precisa reservar e liberar unidades de estoque
 * de tipos de ingresso. A implementação faz a ponte com o módulo de catálogo/ingresso.
 */
public interface GerenciarEstoqueDeIngressosPort {

    void reservarUnidades(Long tipoIngressoId, int quantidade);

    void liberarUnidades(Long tipoIngressoId, int quantidade);
}
