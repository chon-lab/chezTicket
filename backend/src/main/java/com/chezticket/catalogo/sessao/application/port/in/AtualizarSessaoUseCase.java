package com.chezticket.catalogo.sessao.application.port.in;

import com.chezticket.catalogo.sessao.domain.Sessao;

public interface AtualizarSessaoUseCase {

    Sessao atualizar(Long sessaoId, DadosSessao dados);
}
