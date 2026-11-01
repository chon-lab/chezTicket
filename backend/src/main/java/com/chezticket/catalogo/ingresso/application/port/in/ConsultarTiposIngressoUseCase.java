package com.chezticket.catalogo.ingresso.application.port.in;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import java.util.List;

public interface ConsultarTiposIngressoUseCase {

    TipoIngresso buscarPorId(Long id);

    List<TipoIngresso> listarPorSessao(Long sessaoId);
}
