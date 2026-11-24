package com.chezticket.identidade.perfil.application.port.in;

import com.chezticket.identidade.perfil.application.PerfilAgregado;

public interface ConsultarPerfilUseCase {

    PerfilAgregado buscar(Long usuarioId);
}
