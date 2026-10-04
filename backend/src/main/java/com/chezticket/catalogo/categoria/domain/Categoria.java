package com.chezticket.catalogo.categoria.domain;

import com.chezticket.catalogo.categoria.domain.exception.CategoriaInvalidaException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Categoria do catálogo de eventos (ex.: "Shows e Música", "Teatro e Espetáculos").
 *
 * <p>Modelo de domínio puro: nenhuma anotação de framework ou de persistência.
 * As regras de formação (nome obrigatório, tamanho, slug derivado) vivem aqui.
 */
public class Categoria {

    private static final int NOME_MIN = 2;
    private static final int NOME_MAX = 80;

    private Long id;
    private String nome;
    private String slug;
    private boolean ativa;
    private final LocalDateTime criadaEm;

    private Categoria(Long id, String nome, String slug, boolean ativa, LocalDateTime criadaEm) {
        this.id = id;
        this.nome = nome;
        this.slug = slug;
        this.ativa = ativa;
        this.criadaEm = criadaEm;
    }

    /** Cria uma categoria nova, ainda não persistida (sem id). */
    public static Categoria nova(String nome) {
        String nomeNormalizado = normalizarNome(nome);
        return new Categoria(null, nomeNormalizado, Slug.de(nomeNormalizado), true, LocalDateTime.now());
    }

    /** Reconstrói uma categoria já existente. Usado pelos adaptadores de persistência. */
    public static Categoria reconstituir(Long id, String nome, String slug, boolean ativa, LocalDateTime criadaEm) {
        return new Categoria(
                Objects.requireNonNull(id, "id"),
                nome,
                slug,
                ativa,
                Objects.requireNonNull(criadaEm, "criadaEm"));
    }

    public void renomear(String novoNome) {
        String nomeNormalizado = normalizarNome(novoNome);
        this.nome = nomeNormalizado;
        this.slug = Slug.de(nomeNormalizado);
    }

    public void ativar() {
        this.ativa = true;
    }

    public void desativar() {
        this.ativa = false;
    }

    private static String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new CategoriaInvalidaException("O nome da categoria é obrigatório.");
        }
        String limpo = nome.strip().replaceAll("\\s{2,}", " ");
        if (limpo.length() < NOME_MIN || limpo.length() > NOME_MAX) {
            throw new CategoriaInvalidaException(
                    "O nome da categoria deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        return limpo;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String slug() {
        return slug;
    }

    public boolean ativa() {
        return ativa;
    }

    public LocalDateTime criadaEm() {
        return criadaEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria outra)) {
            return false;
        }
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
