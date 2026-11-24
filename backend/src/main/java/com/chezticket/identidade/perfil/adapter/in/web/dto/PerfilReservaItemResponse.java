package com.chezticket.identidade.perfil.adapter.in.web.dto;

import com.chezticket.identidade.perfil.application.ItemDeReservaPerfil;
import com.chezticket.shared.web.UploadUrlResolver;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PerfilReservaItemResponse(
        Long id,
        String status,
        LocalDateTime expiraEm,
        LocalDateTime criadaEm,
        Long eventoId,
        String eventoTitulo,
        String imagemCapa,
        LocalDateTime inicio,
        String local,
        String ingresso,
        int quantidade,
        BigDecimal preco,
        BigDecimal taxaServico) {

    public static PerfilReservaItemResponse de(ItemDeReservaPerfil item, UploadUrlResolver uploads) {
        return new PerfilReservaItemResponse(
                item.reservaId(), item.status().name(), item.expiraEm(), item.criadaEm(), item.eventoId(),
                item.eventoTitulo(), uploads.resolver(item.imagemCapa()), item.inicio(), item.local(),
                item.ingressoNome(), item.quantidade(), item.preco(), item.taxaServico());
    }
}
