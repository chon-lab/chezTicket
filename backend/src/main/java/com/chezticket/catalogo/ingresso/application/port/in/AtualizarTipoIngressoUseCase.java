package com.chezticket.catalogo.ingresso.application.port.in;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;

public interface AtualizarTipoIngressoUseCase {

    TipoIngresso atualizar(Long id, DadosTipoIngresso dados);
}
