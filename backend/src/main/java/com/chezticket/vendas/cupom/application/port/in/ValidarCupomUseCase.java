package com.chezticket.vendas.cupom.application.port.in;

import com.chezticket.vendas.cupom.domain.Cupom;

public interface ValidarCupomUseCase {

    /**
     * @throws com.chezticket.vendas.cupom.domain.exception.CupomNaoEncontradoException código inexistente para o evento
     * @throws com.chezticket.vendas.cupom.domain.exception.CupomExpiradoException cupom expirado
     */
    Cupom validar(Long eventoId, String codigo);
}
