package com.chezticket.catalogo.eventocompleto.adapter.in.web.dto;

import com.chezticket.catalogo.eventocompleto.application.NovoCupomCompleto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CupomCompletoRequest(
        @NotBlank
        String codigo,

        @Min(1)
        @Max(100)
        int percentualDesconto) {

    public NovoCupomCompleto paraComando() {
        return new NovoCupomCompleto(codigo, percentualDesconto);
    }
}
