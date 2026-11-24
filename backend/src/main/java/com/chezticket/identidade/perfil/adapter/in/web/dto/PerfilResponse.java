package com.chezticket.identidade.perfil.adapter.in.web.dto;

import com.chezticket.identidade.conta.adapter.in.web.dto.UsuarioResponse;
import com.chezticket.identidade.perfil.application.PerfilAgregado;
import com.chezticket.shared.web.UploadUrlResolver;
import java.util.List;

public record PerfilResponse(
        UsuarioResponse usuario,
        List<PerfilEventoResponse> eventos,
        List<PerfilReservaItemResponse> reservas) {

    public static PerfilResponse de(PerfilAgregado perfil, UploadUrlResolver uploads) {
        return new PerfilResponse(
                UsuarioResponse.de(perfil.usuario()),
                perfil.eventos().stream().map(e -> PerfilEventoResponse.de(e, uploads)).toList(),
                perfil.itensDeReserva().stream().map(i -> PerfilReservaItemResponse.de(i, uploads)).toList());
    }
}
