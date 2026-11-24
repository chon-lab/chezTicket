package com.chezticket.identidade.perfil.application;

import com.chezticket.catalogo.evento.domain.StatusEvento;
import java.time.LocalDateTime;

public record EventoOrganizadoPerfil(
        Long id,
        String titulo,
        StatusEvento status,
        String imagemCapa,
        LocalDateTime inicio,
        String local) {
}
