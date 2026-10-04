package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;

/** Comando para criar um evento em rascunho. */
public record NovoEvento(
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        String imagemCapa,
        PoliticaReembolso politicaReembolso) {
}
