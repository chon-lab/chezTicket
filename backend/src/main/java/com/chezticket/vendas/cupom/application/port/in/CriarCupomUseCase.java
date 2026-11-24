package com.chezticket.vendas.cupom.application.port.in;

import com.chezticket.vendas.cupom.domain.Cupom;

/** Usado internamente pela orquestração de "evento completo" — não há endpoint público para criar cupom. */
public interface CriarCupomUseCase {

    Cupom criar(NovoCupom comando);
}
