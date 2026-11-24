package com.chezticket.catalogo.eventocompleto.application.port.in;

import com.chezticket.catalogo.eventocompleto.application.EventoCompletoResultado;
import com.chezticket.catalogo.eventocompleto.application.NovoEventoCompleto;

public interface CriarEventoCompletoUseCase {

    EventoCompletoResultado criar(NovoEventoCompleto comando);
}
