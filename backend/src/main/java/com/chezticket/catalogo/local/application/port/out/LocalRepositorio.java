package com.chezticket.catalogo.local.application.port.out;

import com.chezticket.catalogo.local.domain.Local;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de locais. */
public interface LocalRepositorio {

    Local salvar(Local local);

    Optional<Local> buscarPorId(Long id);

    boolean existePorId(Long id);

    List<Local> listarTodos();

    void remover(Long id);
}
