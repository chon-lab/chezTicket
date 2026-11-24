package com.chezticket.vendas.cupom.adapter.in.web.dto;

import com.chezticket.vendas.cupom.domain.Cupom;
import java.time.LocalDate;

public record CupomResponse(String codigo, int percentualDesconto, LocalDate validoAte) {

    public static CupomResponse de(Cupom cupom) {
        return new CupomResponse(cupom.codigo(), cupom.percentualDesconto(), cupom.validoAte());
    }
}
