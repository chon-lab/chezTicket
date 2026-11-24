package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import java.util.List;
import java.util.Map;

/** Comando para criar um evento em rascunho. */
public record NovoEvento(
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        String imagemCapa,
        String imagemDivulgacao,
        PoliticaReembolso politicaReembolso,
        List<String> tags,
        Map<String, String> redesSociais) {
}
