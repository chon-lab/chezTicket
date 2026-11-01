package com.chezticket.vendas.reserva.application.port.in;

/** Varredura periódica: expira reservas ATIVAs cujo prazo já passou e libera o estoque retido. */
public interface ExpirarReservasVencidasUseCase {

    int expirarVencidas();
}
