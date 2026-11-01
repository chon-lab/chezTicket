package com.chezticket.catalogo.ingresso.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

/** Um tipo de ingresso com unidades já vendidas/reservadas não pode ser removido. */
public class TipoIngressoNaoPodeSerRemovidoException extends ConflitoException {

    public TipoIngressoNaoPodeSerRemovidoException(Long id) {
        super("O tipo de ingresso " + id + " já tem unidades vendidas ou reservadas e não pode ser removido.");
    }
}
