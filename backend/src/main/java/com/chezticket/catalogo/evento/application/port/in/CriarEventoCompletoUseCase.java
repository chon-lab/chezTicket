package com.chezticket.catalogo.evento.application.port.in;

public interface CriarEventoCompletoUseCase {

    EventoCompletoCriado criar(NovoEventoCompleto comando);
}
