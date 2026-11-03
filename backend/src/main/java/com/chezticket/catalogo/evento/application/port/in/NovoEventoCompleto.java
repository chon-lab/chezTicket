package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record NovoEventoCompleto(
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        PoliticaReembolso politicaReembolso,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim,
        List<String> tags,
        Map<String, String> redesSociais,
        List<NovoIngresso> ingressos,
        ImagemUpload imagemCapa,
        ImagemUpload imagemDivulgacao,
        boolean publicar) {

    public record NovoIngresso(
            String nome,
            boolean gratuito,
            java.math.BigDecimal preco,
            java.math.BigDecimal taxaServico,
            int quantidadeTotal,
            int limitePorCompra,
            LocalDate inicioVendas,
            LocalDate fimVendas) {
    }
}
