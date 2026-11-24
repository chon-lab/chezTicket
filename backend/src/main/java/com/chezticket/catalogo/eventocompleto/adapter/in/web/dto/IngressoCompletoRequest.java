package com.chezticket.catalogo.eventocompleto.adapter.in.web.dto;

import com.chezticket.catalogo.eventocompleto.application.NovoIngressoCompleto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngressoCompletoRequest(
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
        int limitePorCompra,

        LocalDateTime inicioVendas,

        LocalDateTime fimVendas) {

    public NovoIngressoCompleto paraComando() {
        return new NovoIngressoCompleto(nome, gratuito, preco, taxaServico, quantidadeTotal, limitePorCompra,
                inicioVendas, fimVendas);
    }
}
