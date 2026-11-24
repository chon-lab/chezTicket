package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.domain.Evento;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

final class EventoPersistenceMapper {

    /**
     * {@code tags} e {@code redes_sociais} não têm consulta estruturada — só exibição — então
     * viram JSON em uma coluna de texto em vez de tabelas/colunas próprias. Uma instância
     * estática é segura aqui: {@link ObjectMapper} é thread-safe para (de)serialização.
     */
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<List<String>> TAGS_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<Map<String, String>> REDES_SOCIAIS_TYPE = new TypeReference<>() {
    };

    private EventoPersistenceMapper() {
    }

    static EventoJpaEntity paraEntidade(Evento evento) {
        return new EventoJpaEntity(
                evento.id(),
                evento.organizadorId(),
                evento.categoriaId(),
                evento.titulo(),
                evento.descricao(),
                evento.classificacaoEtaria(),
                evento.imagemCapa(),
                evento.imagemDivulgacao(),
                escrever(evento.tags()),
                escrever(evento.redesSociais()),
                evento.status(),
                evento.politicaReembolso(),
                evento.dataPublicacao(),
                evento.criadoEm(),
                evento.atualizadoEm());
    }

    static Evento paraDominio(EventoJpaEntity entidade) {
        return Evento.reconstituir(
                entidade.getId(),
                entidade.getOrganizadorId(),
                entidade.getCategoriaId(),
                entidade.getTitulo(),
                entidade.getDescricao(),
                entidade.getClassificacaoEtaria(),
                entidade.getImagemCapa(),
                entidade.getImagemDivulgacao(),
                lerTags(entidade.getTags()),
                lerRedesSociais(entidade.getRedesSociais()),
                entidade.getStatus(),
                entidade.getPoliticaReembolso(),
                entidade.getDataPublicacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm());
    }

    private static String escrever(Object valor) {
        try {
            return JSON.writeValueAsString(valor);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar campo do evento para JSON.", e);
        }
    }

    private static List<String> lerTags(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return JSON.readValue(json, TAGS_TYPE);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private static Map<String, String> lerRedesSociais(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return JSON.readValue(json, REDES_SOCIAIS_TYPE);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
