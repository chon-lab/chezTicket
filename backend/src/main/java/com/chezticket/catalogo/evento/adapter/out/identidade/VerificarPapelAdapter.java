package com.chezticket.catalogo.evento.adapter.out.identidade;

import com.chezticket.catalogo.evento.application.port.out.VerificarPapelPort;
import org.springframework.stereotype.Component;

@Component
class VerificarPapelAdapter implements VerificarPapelPort {

    private final AdministradorJpaRepository administradores;

    VerificarPapelAdapter(AdministradorJpaRepository administradores) {
        this.administradores = administradores;
    }

    @Override
    public boolean ehAdministrador(Long usuarioId) {
        return usuarioId != null && administradores.existsById(usuarioId);
    }
}
