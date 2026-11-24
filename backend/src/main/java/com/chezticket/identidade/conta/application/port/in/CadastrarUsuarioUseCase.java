package com.chezticket.identidade.conta.application.port.in;

import com.chezticket.identidade.conta.domain.Usuario;

public interface CadastrarUsuarioUseCase {

    Usuario cadastrar(NovoUsuario comando);
}
