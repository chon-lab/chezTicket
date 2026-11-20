package com.chezticket.catalogo.cupom.application.port.in;

import com.chezticket.catalogo.cupom.domain.Cupom;

public interface ValidarCupomUseCase {

    Cupom validar(Long eventoId, String codigo);
}
