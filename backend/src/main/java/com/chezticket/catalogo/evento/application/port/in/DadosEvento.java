package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;

/** Comando para atualizar os dados descritivos de um evento existente. */
public record DadosEvento(
        Long categoriaId,
        String titulo,
        String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        String imagemCapa,
        PoliticaReembolso politicaReembolso) {
}
