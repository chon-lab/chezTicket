package com.chezticket.catalogo.ingresso.adapter.out.sessao;

import com.chezticket.catalogo.ingresso.application.port.out.VerificarSessaoPort;
import com.chezticket.catalogo.sessao.application.port.out.SessaoRepositorio;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de tipo de ingresso ao módulo de sessão,
 * reusando a porta pública {@link SessaoRepositorio}. Único ponto de
 * acoplamento entre os dois módulos.
 */
@Component
class VerificarSessaoAdapter implements VerificarSessaoPort {

    private final SessaoRepositorio sessoes;

    VerificarSessaoAdapter(SessaoRepositorio sessoes) {
        this.sessoes = sessoes;
    }

    @Override
    public boolean sessaoExiste(Long sessaoId) {
        return sessoes.existePorId(sessaoId);
    }
}
