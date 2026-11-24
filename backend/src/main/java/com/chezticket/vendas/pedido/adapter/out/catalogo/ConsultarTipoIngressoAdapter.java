package com.chezticket.vendas.pedido.adapter.out.catalogo;

import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoEncontradoException;
import com.chezticket.vendas.pedido.application.port.out.ConsultarTipoIngressoPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de pedido ao módulo de tipo de ingresso, reusando a
 * porta pública {@link TipoIngressoRepositorio}.
 */
@Component
class ConsultarTipoIngressoAdapter implements ConsultarTipoIngressoPort {

    private final TipoIngressoRepositorio tiposIngresso;

    ConsultarTipoIngressoAdapter(TipoIngressoRepositorio tiposIngresso) {
        this.tiposIngresso = tiposIngresso;
    }

    @Override
    public TipoIngresso buscarPorId(Long tipoIngressoId) {
        return tiposIngresso.buscarPorId(tipoIngressoId)
                .orElseThrow(() -> new TipoIngressoNaoEncontradoException(tipoIngressoId));
    }
}
