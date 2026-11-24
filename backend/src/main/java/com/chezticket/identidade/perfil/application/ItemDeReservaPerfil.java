package com.chezticket.identidade.perfil.application;

import com.chezticket.vendas.reserva.domain.StatusReserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Uma linha achatada: reserva + um dos seus itens, com os dados do evento/sessão/ingresso já resolvidos. */
public record ItemDeReservaPerfil(
        Long reservaId,
        StatusReserva status,
        LocalDateTime expiraEm,
        LocalDateTime criadaEm,
        Long eventoId,
        String eventoTitulo,
        String imagemCapa,
        LocalDateTime inicio,
        String local,
        String ingressoNome,
        int quantidade,
        BigDecimal preco,
        BigDecimal taxaServico) {
}
