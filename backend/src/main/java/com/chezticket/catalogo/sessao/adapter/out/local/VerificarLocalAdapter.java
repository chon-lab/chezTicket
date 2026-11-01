package com.chezticket.catalogo.sessao.adapter.out.local;

import com.chezticket.catalogo.local.application.port.out.LocalRepositorio;
import com.chezticket.catalogo.sessao.application.port.out.VerificarLocalPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de sessão ao módulo de local,
 * reusando a porta pública {@link LocalRepositorio}. Único ponto de
 * acoplamento entre os dois módulos.
 */
@Component
class VerificarLocalAdapter implements VerificarLocalPort {

    private final LocalRepositorio locais;

    VerificarLocalAdapter(LocalRepositorio locais) {
        this.locais = locais;
    }

    @Override
    public boolean localExiste(Long localId) {
        return locais.existePorId(localId);
    }
}
