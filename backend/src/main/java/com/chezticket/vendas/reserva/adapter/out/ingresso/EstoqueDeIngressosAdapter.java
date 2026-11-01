package com.chezticket.vendas.reserva.adapter.out.ingresso;

import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoEncontradoException;
import com.chezticket.vendas.reserva.application.port.out.GerenciarEstoqueDeIngressosPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de reserva ao módulo de tipo de ingresso,
 * reusando a porta pública {@link TipoIngressoRepositorio}. Único ponto de
 * acoplamento entre os dois módulos.
 */
@Component
class EstoqueDeIngressosAdapter implements GerenciarEstoqueDeIngressosPort {

    private final TipoIngressoRepositorio tiposIngresso;

    EstoqueDeIngressosAdapter(TipoIngressoRepositorio tiposIngresso) {
        this.tiposIngresso = tiposIngresso;
    }

    @Override
    public void reservarUnidades(Long tipoIngressoId, int quantidade) {
        TipoIngresso tipoIngresso = carregar(tipoIngressoId);
        tipoIngresso.reservarUnidades(quantidade);
        tiposIngresso.salvar(tipoIngresso);
    }

    @Override
    public void liberarUnidades(Long tipoIngressoId, int quantidade) {
        TipoIngresso tipoIngresso = carregar(tipoIngressoId);
        tipoIngresso.liberarUnidades(quantidade);
        tiposIngresso.salvar(tipoIngresso);
    }

    private TipoIngresso carregar(Long tipoIngressoId) {
        return tiposIngresso.buscarPorId(tipoIngressoId)
                .orElseThrow(() -> new TipoIngressoNaoEncontradoException(tipoIngressoId));
    }
}
