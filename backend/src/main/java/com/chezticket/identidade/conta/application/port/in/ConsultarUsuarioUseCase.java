package com.chezticket.identidade.conta.application.port.in;

import com.chezticket.identidade.conta.domain.Usuario;

public interface ConsultarUsuarioUseCase {

    Usuario buscarPorId(Long id);
}
