package com.chezticket.catalogo.ingresso.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

/** A reserva/venda foi tentada fora da janela de vendas configurada para o tipo de ingresso. */
public class ForaDoPeriodoDeVendasException extends ConflitoException {

    public ForaDoPeriodoDeVendasException(Long tipoIngressoId) {
        super("As vendas do tipo de ingresso " + tipoIngressoId + " não estão abertas no momento.");
    }
}
