package com.chezticket.catalogo.local.application.port.in;

import com.chezticket.catalogo.local.domain.Local;

public interface CadastrarLocalUseCase {

    Local cadastrar(NovoLocal comando);
}
