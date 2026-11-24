package com.chezticket.catalogo.evento.application.port.in;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import java.util.List;
import java.util.Map;

/** Comando para atualizar os dados descritivos de um evento existente. */
public record DadosEvento(
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
