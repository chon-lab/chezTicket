package com.chezticket.catalogo.ingresso.adapter.in.web.dto;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TipoIngressoResponse(
        Long id,
        Long sessaoId,
        String nome,
        boolean gratuito,
        BigDecimal preco,
        BigDecimal taxaServico,
        int quantidadeTotal,
        int quantidadeVendida,
        int quantidadeDisponivel,
        int limitePorCompra,
        LocalDate inicioVendas,
        LocalDate fimVendas) {

    public static TipoIngressoResponse de(TipoIngresso t) {
        return new TipoIngressoResponse(
                t.id(), t.sessaoId(), t.nome(), t.gratuito(), t.preco(), t.taxaServico(),
                t.quantidadeTotal(), t.quantidadeVendida(), t.quantidadeDisponivel(), t.limitePorCompra(),
                t.inicioVendas(), t.fimVendas());
    }
}
