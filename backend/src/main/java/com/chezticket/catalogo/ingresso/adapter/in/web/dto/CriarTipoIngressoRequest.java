package com.chezticket.catalogo.ingresso.adapter.in.web.dto;

import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CriarTipoIngressoRequest(
        @NotNull
        @Schema(description = "ID da sessão à qual este tipo de ingresso pertence", example = "1")
        Long sessaoId,

        @NotBlank
        @Size(max = 80)
        @Schema(example = "Inteira")
        String nome,

        @Schema(description = "Se verdadeiro, preço e taxa de serviço são zerados", example = "false")
        boolean gratuito,

        @PositiveOrZero
        @Schema(example = "120.00")
        BigDecimal preco,

        @PositiveOrZero
        @Schema(example = "12.00")
        BigDecimal taxaServico,

        @Positive
        @Schema(example = "200")
        int quantidadeTotal,

        @Positive
        @Schema(description = "Padrão sugerido: 5", example = "5")
        int limitePorCompra) {

    public NovoTipoIngresso paraComando() {
        return new NovoTipoIngresso(sessaoId, nome, gratuito, preco, taxaServico, quantidadeTotal, limitePorCompra);
    }
}
