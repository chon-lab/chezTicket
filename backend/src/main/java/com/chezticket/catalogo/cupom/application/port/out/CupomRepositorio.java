package com.chezticket.catalogo.cupom.application.port.out;

import com.chezticket.catalogo.cupom.domain.Cupom;
import java.util.Optional;

public interface CupomRepositorio {

    boolean existePorCodigo(String codigo);

    Optional<Cupom> buscarPorCodigo(String codigo);

    Cupom salvar(Cupom cupom);
}
