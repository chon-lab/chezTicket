package com.chezticket.catalogo.ingresso.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Comando para cadastrar um tipo de ingresso novo para uma sessão. */
public record NovoTipoIngresso(
        Long sessaoId,
        String nome,
        boolean gratuito,
        BigDecimal preco,
        BigDecimal taxaServico,
        int quantidadeTotal,
        int limitePorCompra,
        LocalDate inicioVendas,
        LocalDate fimVendas) {

    public NovoTipoIngresso(Long sessaoId, String nome, boolean gratuito, BigDecimal preco,
            BigDecimal taxaServico, int quantidadeTotal, int limitePorCompra) {
        this(sessaoId, nome, gratuito, preco, taxaServico, quantidadeTotal, limitePorCompra, null, null);
    }
}
