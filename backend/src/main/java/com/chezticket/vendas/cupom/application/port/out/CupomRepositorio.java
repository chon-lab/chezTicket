package com.chezticket.vendas.cupom.application.port.out;

import com.chezticket.vendas.cupom.domain.Cupom;
import java.util.Optional;

public interface CupomRepositorio {

    Cupom salvar(Cupom cupom);

    Optional<Cupom> buscarPorEventoECodigo(Long eventoId, String codigo);

    boolean existePorCodigo(String codigo);
}
