package com.chezticket.catalogo.evento.domain;

import com.chezticket.catalogo.evento.domain.exception.EventoInvalidoException;
import com.chezticket.catalogo.evento.domain.exception.TransicaoDeStatusInvalidaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Evento publicável no catálogo. Modelo de domínio puro.
 *
 * <p>Concentra as regras de formação (título, categoria, organizador obrigatórios) e as
 * transições de status do ciclo de vida (Figura 7): RASCUNHO &rarr; PUBLICADO &rarr;
 * ENCERRADO, com CANCELADO acessível a partir de qualquer estado não terminal.
 */
public class Evento {

    private static final int TITULO_MIN = 3;
    private static final int TITULO_MAX = 160;
    private static final Set<StatusEvento> EDITAVEIS = EnumSet.of(StatusEvento.RASCUNHO, StatusEvento.PUBLICADO);

    private Long id;
    private final Long organizadorId;
    private Long categoriaId;
    private String titulo;
    private String descricao;
    private ClassificacaoEtaria classificacaoEtaria;
    private String imagemCapa;
    private String imagemDivulgacao;
    private final List<String> tags;
    private final Map<String, String> redesSociais;
    private StatusEvento status;
    private PoliticaReembolso politicaReembolso;
    private LocalDateTime dataPublicacao;
    private final LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    private Evento(Long id, Long organizadorId, Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa, String imagemDivulgacao,
            List<String> tags, Map<String, String> redesSociais, StatusEvento status,
            PoliticaReembolso politicaReembolso, LocalDateTime dataPublicacao,
            LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.organizadorId = organizadorId;
        this.categoriaId = categoriaId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.classificacaoEtaria = classificacaoEtaria;
        this.imagemCapa = imagemCapa;
        this.imagemDivulgacao = imagemDivulgacao;
        this.tags = normalizarTags(tags);
        this.redesSociais = normalizarRedesSociais(redesSociais);
        this.status = status;
        this.politicaReembolso = politicaReembolso;
        this.dataPublicacao = dataPublicacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    /** Cria um evento novo em RASCUNHO. */
    public static Evento criarRascunho(Long organizadorId, Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa, PoliticaReembolso politicaReembolso) {
        return criarRascunho(organizadorId, categoriaId, titulo, descricao, classificacaoEtaria, imagemCapa,
                null, List.of(), Map.of(), politicaReembolso);
    }

    public static Evento criarRascunho(Long organizadorId, Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa, String imagemDivulgacao,
            List<String> tags, Map<String, String> redesSociais, PoliticaReembolso politicaReembolso) {
        exigir(organizadorId != null, "O organizador do evento é obrigatório.");
        exigir(categoriaId != null, "A categoria do evento é obrigatória.");
        LocalDateTime agora = LocalDateTime.now();
        return new Evento(
                null,
                organizadorId,
                categoriaId,
                validarTitulo(titulo),
                normalizar(descricao),
                classificacaoEtaria != null ? classificacaoEtaria : ClassificacaoEtaria.LIVRE,
                normalizar(imagemCapa),
                normalizar(imagemDivulgacao),
                tags,
                redesSociais,
                StatusEvento.RASCUNHO,
                politicaReembolso != null ? politicaReembolso : PoliticaReembolso.PADRAO,
                null,
                agora,
                agora);
    }

    /** Reconstrói um evento existente (usado pelo adaptador de persistência). */
    public static Evento reconstituir(Long id, Long organizadorId, Long categoriaId, String titulo,
            String descricao, ClassificacaoEtaria classificacaoEtaria, String imagemCapa, StatusEvento status,
            PoliticaReembolso politicaReembolso, LocalDateTime dataPublicacao, LocalDateTime criadoEm,
            LocalDateTime atualizadoEm) {
        return reconstituir(id, organizadorId, categoriaId, titulo, descricao, classificacaoEtaria, imagemCapa,
                null, List.of(), Map.of(), status, politicaReembolso, dataPublicacao, criadoEm, atualizadoEm);
    }

    public static Evento reconstituir(Long id, Long organizadorId, Long categoriaId, String titulo,
            String descricao, ClassificacaoEtaria classificacaoEtaria, String imagemCapa,
            String imagemDivulgacao, List<String> tags, Map<String, String> redesSociais, StatusEvento status,
            PoliticaReembolso politicaReembolso, LocalDateTime dataPublicacao, LocalDateTime criadoEm,
            LocalDateTime atualizadoEm) {
        return new Evento(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(organizadorId, "organizadorId"),
                categoriaId,
                titulo,
                descricao,
                classificacaoEtaria,
                imagemCapa,
                imagemDivulgacao,
                tags,
                redesSociais,
                Objects.requireNonNull(status, "status"),
                politicaReembolso,
                dataPublicacao,
                Objects.requireNonNull(criadoEm, "criadoEm"),
                atualizadoEm);
    }

    /** Edita os dados descritivos. Permitido apenas enquanto RASCUNHO ou PUBLICADO. */
    public void atualizarDados(Long categoriaId, String titulo, String descricao,
            ClassificacaoEtaria classificacaoEtaria, String imagemCapa, PoliticaReembolso politicaReembolso) {
        if (!EDITAVEIS.contains(status)) {
            throw new EventoInvalidoException("Um evento " + status + " não pode ser editado.");
        }
        exigir(categoriaId != null, "A categoria do evento é obrigatória.");
        this.categoriaId = categoriaId;
        this.titulo = validarTitulo(titulo);
        this.descricao = normalizar(descricao);
        if (classificacaoEtaria != null) {
            this.classificacaoEtaria = classificacaoEtaria;
        }
        this.imagemCapa = normalizar(imagemCapa);
        if (politicaReembolso != null) {
            this.politicaReembolso = politicaReembolso;
        }
        this.atualizadoEm = LocalDateTime.now();
    }

    public void publicar() {
        if (status != StatusEvento.RASCUNHO) {
            throw new TransicaoDeStatusInvalidaException(status, StatusEvento.PUBLICADO);
        }
        exigir(descricao != null && !descricao.isBlank(), "Descreva o evento antes de publicá-lo.");
        this.status = StatusEvento.PUBLICADO;
        this.dataPublicacao = LocalDateTime.now();
        this.atualizadoEm = this.dataPublicacao;
    }

    public void cancelar() {
        if (status == StatusEvento.CANCELADO || status == StatusEvento.ENCERRADO) {
            throw new TransicaoDeStatusInvalidaException(status, StatusEvento.CANCELADO);
        }
        this.status = StatusEvento.CANCELADO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void encerrar() {
        if (status != StatusEvento.PUBLICADO && status != StatusEvento.ESGOTADO) {
            throw new TransicaoDeStatusInvalidaException(status, StatusEvento.ENCERRADO);
        }
        this.status = StatusEvento.ENCERRADO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean podeSerRemovido() {
        return status == StatusEvento.RASCUNHO;
    }

    // ------------------------------------------------------------------
    // Validações internas
    // ------------------------------------------------------------------
    private static String validarTitulo(String titulo) {
        String limpo = titulo == null ? "" : titulo.strip().replaceAll("\\s{2,}", " ");
        if (limpo.length() < TITULO_MIN || limpo.length() > TITULO_MAX) {
            throw new EventoInvalidoException(
                    "O título deve ter entre " + TITULO_MIN + " e " + TITULO_MAX + " caracteres.");
        }
        return limpo;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpo = texto.strip();
        return limpo.isEmpty() ? null : limpo;
    }

    private static List<String> normalizarTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .toList();
    }

    private static Map<String, String> normalizarRedesSociais(Map<String, String> redesSociais) {
        if (redesSociais == null || redesSociais.isEmpty()) {
            return Map.of();
        }
        Map<String, String> links = new LinkedHashMap<>();
        redesSociais.forEach((rede, url) -> {
            if (rede != null && url != null && !url.isBlank()) {
                links.put(rede, url.strip());
            }
        });
        return Map.copyOf(links);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new EventoInvalidoException(mensagem);
        }
    }

    // ------------------------------------------------------------------
    // Acessores
    // ------------------------------------------------------------------
    public Long id() {
        return id;
    }

    public Long organizadorId() {
        return organizadorId;
    }

    public Long categoriaId() {
        return categoriaId;
    }

    public String titulo() {
        return titulo;
    }

    public String descricao() {
        return descricao;
    }

    public ClassificacaoEtaria classificacaoEtaria() {
        return classificacaoEtaria;
    }

    public String imagemCapa() {
        return imagemCapa;
    }

    public String imagemDivulgacao() {
        return imagemDivulgacao;
    }

    public List<String> tags() {
        return tags;
    }

    public Map<String, String> redesSociais() {
        return redesSociais;
    }

    public StatusEvento status() {
        return status;
    }

    public PoliticaReembolso politicaReembolso() {
        return politicaReembolso;
    }

    public LocalDateTime dataPublicacao() {
        return dataPublicacao;
    }

    public LocalDateTime criadoEm() {
        return criadoEm;
    }

    public LocalDateTime atualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
