package com.chezticket.catalogo.cupom.adapter.in.web.dto;

import com.chezticket.catalogo.cupom.domain.Cupom;
import java.time.LocalDateTime;

public record CupomResponse(String codigo, int percentualDesconto, LocalDateTime validoAte) {

    public static CupomResponse de(Cupom cupom) {
        return new CupomResponse(cupom.codigo(), cupom.percentualDesconto(), cupom.validoAte());
    }
}
