package com.chezticket.vendas.cupom.application.port.in;

import java.time.LocalDate;

public record NovoCupom(Long eventoId, String codigo, int percentualDesconto, LocalDate validoAte) {
}
