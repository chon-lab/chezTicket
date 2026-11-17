package com.chezticket.catalogo.cupom.domain;

import java.time.LocalDateTime;

public record Cupom(
        Long id,
        Long eventoId,
        String codigo,
        int percentualDesconto,
        LocalDateTime validoAte) {
}
