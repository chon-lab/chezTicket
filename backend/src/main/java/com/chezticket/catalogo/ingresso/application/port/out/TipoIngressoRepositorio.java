package com.chezticket.catalogo.ingresso.application.port.out;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de tipos de ingresso. */
public interface TipoIngressoRepositorio {

    TipoIngresso salvar(TipoIngresso tipoIngresso);

    Optional<TipoIngresso> buscarPorId(Long id);

    List<TipoIngresso> listarPorSessao(Long sessaoId);

    void remover(Long id);
}
