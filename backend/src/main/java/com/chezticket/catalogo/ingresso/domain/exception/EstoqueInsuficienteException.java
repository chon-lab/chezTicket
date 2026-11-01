package com.chezticket.catalogo.ingresso.domain.exception;

import com.chezticket.shared.domain.ConflitoException;

/** Não há unidades suficientes disponíveis para atender a quantidade pedida. */
public class EstoqueInsuficienteException extends ConflitoException {

    public EstoqueInsuficienteException(Long tipoIngressoId, int quantidadeDisponivel, int quantidadePedida) {
        super("Estoque insuficiente para o tipo de ingresso " + tipoIngressoId + ": disponível "
                + quantidadeDisponivel + ", pedido " + quantidadePedida + ".");
    }
}
