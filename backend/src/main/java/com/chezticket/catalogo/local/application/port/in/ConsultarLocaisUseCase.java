package com.chezticket.catalogo.local.application.port.in;

import com.chezticket.catalogo.local.domain.Local;
import java.util.List;

public interface ConsultarLocaisUseCase {

    Local buscarPorId(Long id);

    List<Local> listar();
}
