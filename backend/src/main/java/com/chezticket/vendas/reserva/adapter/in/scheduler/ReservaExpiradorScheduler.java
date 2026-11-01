package com.chezticket.vendas.reserva.adapter.in.scheduler;

import com.chezticket.vendas.reserva.application.port.in.ExpirarReservasVencidasUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada "orientado pelo relógio": varre periodicamente as reservas
 * vencidas para devolver o estoque retido, em vez de deixá-lo preso até alguém
 * consultar a reserva (controle de estoque e disponibilidade do cronograma).
 */
@Component
class ReservaExpiradorScheduler {

    private final ExpirarReservasVencidasUseCase expirarReservasVencidas;

    ReservaExpiradorScheduler(ExpirarReservasVencidasUseCase expirarReservasVencidas) {
        this.expirarReservasVencidas = expirarReservasVencidas;
    }

    @Scheduled(fixedDelay = 60_000)
    void expirarReservasVencidas() {
        expirarReservasVencidas.expirarVencidas();
    }
}
