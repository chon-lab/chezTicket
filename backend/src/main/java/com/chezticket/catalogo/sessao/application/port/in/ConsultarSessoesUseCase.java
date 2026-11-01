package com.chezticket.catalogo.sessao.application.port.in;

import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;

public interface ConsultarSessoesUseCase {

    Sessao buscarPorId(Long id);

    List<Sessao> listar(FiltroSessoes filtro);
}
