package com.chezticket.catalogo.ingresso.application.port.in;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;

public interface CadastrarTipoIngressoUseCase {

    TipoIngresso cadastrar(NovoTipoIngresso comando);
}
