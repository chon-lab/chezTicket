package com.chezticket.catalogo.sessao.application.port.in;

import com.chezticket.catalogo.sessao.domain.Sessao;

public interface GerenciarCicloDeVidaDaSessaoUseCase {

    Sessao iniciar(Long sessaoId);

    Sessao encerrar(Long sessaoId);

    Sessao cancelar(Long sessaoId);
}
