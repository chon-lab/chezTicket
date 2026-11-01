package com.chezticket.catalogo.local.domain;

import com.chezticket.catalogo.local.domain.exception.LocalInvalidoException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Local (venue) onde sessões de eventos acontecem. Modelo de domínio puro.
 *
 * <p>Concentra as regras de formação (nome, endereço e capacidade obrigatórios).
 * Não tem ciclo de vida próprio — passa a ser referenciado por {@code Sessao}.
 */
public class Local {

    private static final int NOME_MIN = 2;
    private static final int NOME_MAX = 160;

    private Long id;
    private String nome;
    private String endereco;
    private String cidade;
    private String uf;
    private int capacidadeTotal;
    private final LocalDateTime criadoEm;

    private Local(Long id, String nome, String endereco, String cidade, String uf, int capacidadeTotal,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.uf = uf;
        this.capacidadeTotal = capacidadeTotal;
        this.criadoEm = criadoEm;
    }

    /** Cria um local novo, ainda não persistido (sem id). */
    public static Local novo(String nome, String endereco, String cidade, String uf, int capacidadeTotal) {
        return new Local(
                null,
                validarNome(nome),
                validarObrigatorio(endereco, "O endereço do local é obrigatório."),
                validarObrigatorio(cidade, "A cidade do local é obrigatória."),
                validarUf(uf),
                validarCapacidade(capacidadeTotal),
                LocalDateTime.now());
    }

    /** Reconstrói um local já existente. Usado pelo adaptador de persistência. */
    public static Local reconstituir(Long id, String nome, String endereco, String cidade, String uf,
            int capacidadeTotal, LocalDateTime criadoEm) {
        return new Local(
                Objects.requireNonNull(id, "id"),
                nome,
                endereco,
                cidade,
                uf,
                capacidadeTotal,
                Objects.requireNonNull(criadoEm, "criadoEm"));
    }

    public void atualizarDados(String nome, String endereco, String cidade, String uf, int capacidadeTotal) {
        this.nome = validarNome(nome);
        this.endereco = validarObrigatorio(endereco, "O endereço do local é obrigatório.");
        this.cidade = validarObrigatorio(cidade, "A cidade do local é obrigatória.");
        this.uf = validarUf(uf);
        this.capacidadeTotal = validarCapacidade(capacidadeTotal);
    }

    private static String validarNome(String nome) {
        String limpo = nome == null ? "" : nome.strip().replaceAll("\\s{2,}", " ");
        if (limpo.length() < NOME_MIN || limpo.length() > NOME_MAX) {
            throw new LocalInvalidoException(
                    "O nome do local deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        return limpo;
    }

    private static String validarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new LocalInvalidoException(mensagem);
        }
        return valor.strip();
    }

    private static String validarUf(String uf) {
        String limpa = uf == null ? "" : uf.strip().toUpperCase();
        if (!limpa.matches("[A-Z]{2}")) {
            throw new LocalInvalidoException("A UF deve ter exatamente 2 letras (ex.: RJ).");
        }
        return limpa;
    }

    private static int validarCapacidade(int capacidadeTotal) {
        if (capacidadeTotal <= 0) {
            throw new LocalInvalidoException("A capacidade total do local deve ser maior que zero.");
        }
        return capacidadeTotal;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String endereco() {
        return endereco;
    }

    public String cidade() {
        return cidade;
    }

    public String uf() {
        return uf;
    }

    public int capacidadeTotal() {
        return capacidadeTotal;
    }

    public LocalDateTime criadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Local outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
