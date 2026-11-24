package com.chezticket.vendas.pedido.application.port.out;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;

/**
 * Porta de saída: liga o módulo de pedido ao módulo de tipo de ingresso, para obter nome,
 * preço e taxa de serviço no momento da compra.
 */
public interface ConsultarTipoIngressoPort {

    TipoIngresso buscarPorId(Long tipoIngressoId);
}
