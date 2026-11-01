package com.chezticket.catalogo.ingresso.application.port.in;

import java.math.BigDecimal;

/** Comando para atualizar os dados de um tipo de ingresso existente. */
public record DadosTipoIngresso(
        String nome,
        boolean gratuito,
        BigDecimal preco,
        BigDecimal taxaServico,
        int quantidadeTotal,
        int limitePorCompra) {
}
