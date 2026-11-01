package com.chezticket.catalogo.ingresso.adapter.in.web.dto;

import com.chezticket.catalogo.ingresso.application.port.in.DadosTipoIngresso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AtualizarTipoIngressoRequest(
        @NotBlank
        @Size(max = 80)
        String nome,

        boolean gratuito,

        @PositiveOrZero
        BigDecimal preco,

        @PositiveOrZero
        BigDecimal taxaServico,

        @Positive
        int quantidadeTotal,

        @Positive
        int limitePorCompra) {

    public DadosTipoIngresso paraComando() {
        return new DadosTipoIngresso(nome, gratuito, preco, taxaServico, quantidadeTotal, limitePorCompra);
    }
}
