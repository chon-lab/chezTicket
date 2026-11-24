package com.chezticket.identidade.perfil.adapter.in.web.dto;

import com.chezticket.identidade.perfil.application.EventoOrganizadoPerfil;
import com.chezticket.shared.web.UploadUrlResolver;
import java.time.LocalDateTime;

public record PerfilEventoResponse(
        Long id,
        String titulo,
        String status,
        String imagemCapa,
        LocalDateTime inicio,
        String local) {

    public static PerfilEventoResponse de(EventoOrganizadoPerfil evento, UploadUrlResolver uploads) {
        return new PerfilEventoResponse(
                evento.id(), evento.titulo(), evento.status().name(), uploads.resolver(evento.imagemCapa()),
                evento.inicio(), evento.local());
    }
}
