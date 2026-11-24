package com.chezticket.catalogo.eventocompleto.application;

import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Comando que orquestra a criação de um evento completo (evento + sessão + tipos de ingresso +
 * cupom opcional) numa única operação — o que o assistente de criação do front faz em uma
 * chamada só. {@code organizadorId} vem da sessão autenticada, nunca do cliente.
 */
public record NovoEventoCompleto(
        Long organizadorId,
        Long categoriaId,
        String titulo,
        String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        PoliticaReembolso politicaReembolso,
        String imagemCapa,
        String imagemDivulgacao,
        List<String> tags,
        Map<String, String> redesSociais,
        String endereco,
        LocalDateTime inicio,
        LocalDateTime fim,
        List<NovoIngressoCompleto> ingressos,
        NovoCupomCompleto cupom,
        boolean publicar) {
}
