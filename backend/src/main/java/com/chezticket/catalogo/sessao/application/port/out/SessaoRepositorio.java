package com.chezticket.catalogo.sessao.application.port.out;

import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de sessões. */
public interface SessaoRepositorio {

    Sessao salvar(Sessao sessao);

    Optional<Sessao> buscarPorId(Long id);

    boolean existePorId(Long id);

    List<Sessao> buscar(FiltroSessoes filtro);

    void remover(Long id);
}
