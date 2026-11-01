package com.chezticket.catalogo.sessao.application.port.in;

import com.chezticket.catalogo.sessao.domain.Sessao;

public interface AgendarSessaoUseCase {

    Sessao agendar(NovaSessao comando);
}
