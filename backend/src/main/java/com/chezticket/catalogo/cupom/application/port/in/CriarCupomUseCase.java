package com.chezticket.catalogo.cupom.application.port.in;

import com.chezticket.catalogo.cupom.domain.Cupom;
import java.time.LocalDateTime;

public interface CriarCupomUseCase {

    Cupom criar(Long eventoId, String codigo, int percentualDesconto, LocalDateTime validoAte);
}
