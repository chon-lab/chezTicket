package com.chezticket.identidade.conta.adapter.in.web.dto;

import com.chezticket.identidade.conta.domain.Usuario;

public record UsuarioResponse(Long id, String nome, String email, String cpf) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.id(), usuario.nome(), usuario.email(), usuario.cpf());
    }
}
