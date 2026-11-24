package com.chezticket.identidade.conta.domain;

import com.chezticket.identidade.conta.domain.exception.UsuarioInvalidoException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Conta de usuário do chezTicket. Modelo de domínio puro.
 *
 * <p>No modelo simplificado adotado para o front (sem um fluxo separado de "tornar-se
 * organizador"), toda conta cadastrada funciona ao mesmo tempo como participante (compra
 * ingressos) e organizador (cria eventos) — é o adaptador de persistência que grava as três
 * linhas ({@code usuario}, {@code participante}, {@code organizador}); este domínio concentra
 * só os dados de identidade e credencial.
 */
public class Usuario {

    private static final int NOME_MIN = 2;
    private static final int NOME_MAX = 120;
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern CPF_DIGITOS = Pattern.compile("^\\d{11}$");

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String cpf;
    private StatusUsuario status;
    private final LocalDateTime criadoEm;

    private Usuario(Long id, String nome, String email, String senhaHash, String cpf, StatusUsuario status,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.cpf = cpf;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    /** Cadastra uma conta nova. {@code senhaHash} já deve chegar hasheada (ver {@code HashDeSenhaPort}). */
    public static Usuario cadastrar(String nome, String email, String senhaHash, String cpf) {
        exigir(senhaHash != null && !senhaHash.isBlank(), "A senha é obrigatória.");
        return new Usuario(
                null,
                validarNome(nome),
                validarEmail(email),
                senhaHash,
                validarCpf(cpf),
                StatusUsuario.ATIVO,
                LocalDateTime.now());
    }

    /** Reconstrói uma conta já existente. Usado pelo adaptador de persistência. */
    public static Usuario reconstituir(Long id, String nome, String email, String senhaHash, String cpf,
            StatusUsuario status, LocalDateTime criadoEm) {
        return new Usuario(
                Objects.requireNonNull(id, "id"),
                nome,
                email,
                senhaHash,
                cpf,
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(criadoEm, "criadoEm"));
    }

    private static String validarNome(String nome) {
        String limpo = nome == null ? "" : nome.strip().replaceAll("\\s{2,}", " ");
        if (limpo.length() < NOME_MIN || limpo.length() > NOME_MAX) {
            throw new UsuarioInvalidoException(
                    "O nome deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        return limpo;
    }

    private static String validarEmail(String email) {
        String limpo = email == null ? "" : email.strip().toLowerCase();
        if (!EMAIL.matcher(limpo).matches()) {
            throw new UsuarioInvalidoException("Informe um e-mail válido.");
        }
        return limpo;
    }

    private static String validarCpf(String cpf) {
        String digitos = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (!CPF_DIGITOS.matcher(digitos).matches()) {
            throw new UsuarioInvalidoException("Informe um CPF com 11 dígitos.");
        }
        return digitos;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new UsuarioInvalidoException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String email() {
        return email;
    }

    public String senhaHash() {
        return senhaHash;
    }

    public String cpf() {
        return cpf;
    }

    public StatusUsuario status() {
        return status;
    }

    public LocalDateTime criadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
