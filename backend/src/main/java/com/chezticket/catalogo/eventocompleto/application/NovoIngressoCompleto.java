package com.chezticket.catalogo.eventocompleto.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NovoIngressoCompleto(
        String nome,
        boolean gratuito,
        BigDecimal preco,
        BigDecimal taxaServico,
        int quantidadeTotal,
        int limitePorCompra,
        LocalDateTime inicioVendas,
        LocalDateTime fimVendas) {
}
