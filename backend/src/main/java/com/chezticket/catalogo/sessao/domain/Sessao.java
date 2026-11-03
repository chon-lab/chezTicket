package com.chezticket.catalogo.sessao.domain;

import com.chezticket.catalogo.sessao.domain.exception.SessaoInvalidaException;
import com.chezticket.catalogo.sessao.domain.exception.TransicaoDeStatusDaSessaoInvalidaException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Sessão (ocorrência) de um evento, em um local e horário específicos. Modelo de domínio puro.
 *
 * <p>Um evento tem uma ou mais sessões (Figura 2); cada sessão concentra os tipos de ingresso
 * vendidos para aquela ocorrência.
 */
public class Sessao {

    private Long id;
    private final Long eventoId;
    private Long localId;
    private String endereco;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusSessao status;

    private Sessao(Long id, Long eventoId, Long localId, String endereco,
            LocalDateTime inicio, LocalDateTime fim, StatusSessao status) {
        this.id = id;
        this.eventoId = eventoId;
        this.localId = localId;
        this.endereco = normalizarEndereco(endereco);
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
    }

    /** Agenda uma sessão nova para um evento. */
    public static Sessao agendar(Long eventoId, Long localId, LocalDateTime inicio, LocalDateTime fim) {
        return criar(eventoId, localId, null, inicio, fim);
    }

    public static Sessao agendarComEndereco(
            Long eventoId, String endereco, LocalDateTime inicio, LocalDateTime fim) {
        return criar(eventoId, null, endereco, inicio, fim);
    }

    private static Sessao criar(
            Long eventoId, Long localId, String endereco, LocalDateTime inicio, LocalDateTime fim) {
        exigir(eventoId != null, "O evento da sessão é obrigatório.");
        exigir(localId != null || normalizarEndereco(endereco) != null,
                "Informe um local cadastrado ou o endereço da sessão.");
        validarPeriodo(inicio, fim);
        return new Sessao(null, eventoId, localId, endereco, inicio, fim, StatusSessao.AGENDADA);
    }

    /** Reconstrói uma sessão já existente. Usado pelo adaptador de persistência. */
    public static Sessao reconstituir(Long id, Long eventoId, Long localId, LocalDateTime inicio,
            LocalDateTime fim, StatusSessao status) {
        return reconstituir(id, eventoId, localId, null, inicio, fim, status);
    }

    public static Sessao reconstituir(Long id, Long eventoId, Long localId, String endereco,
            LocalDateTime inicio, LocalDateTime fim, StatusSessao status) {
        return new Sessao(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(eventoId, "eventoId"),
                localId,
                endereco,
                inicio,
                fim,
                Objects.requireNonNull(status, "status"));
    }

    /** Reagenda local/horário. Permitido apenas enquanto AGENDADA. */
    public void atualizarDados(Long localId, LocalDateTime inicio, LocalDateTime fim) {
        if (status != StatusSessao.AGENDADA) {
            throw new SessaoInvalidaException("Uma sessão " + status + " não pode ser reagendada.");
        }
        exigir(localId != null, "O local da sessão é obrigatório.");
        validarPeriodo(inicio, fim);
        this.localId = localId;
        this.endereco = null;
        this.inicio = inicio;
        this.fim = fim;
    }

    public void iniciar() {
        if (status != StatusSessao.AGENDADA) {
            throw new TransicaoDeStatusDaSessaoInvalidaException(status, StatusSessao.EM_ANDAMENTO);
        }
        this.status = StatusSessao.EM_ANDAMENTO;
    }

    public void encerrar() {
        if (status != StatusSessao.EM_ANDAMENTO) {
            throw new TransicaoDeStatusDaSessaoInvalidaException(status, StatusSessao.ENCERRADA);
        }
        this.status = StatusSessao.ENCERRADA;
    }

    public void cancelar() {
        if (status != StatusSessao.AGENDADA && status != StatusSessao.EM_ANDAMENTO) {
            throw new TransicaoDeStatusDaSessaoInvalidaException(status, StatusSessao.CANCELADA);
        }
        this.status = StatusSessao.CANCELADA;
    }

    public boolean podeSerRemovida() {
        return status == StatusSessao.AGENDADA;
    }

    private static void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        exigir(inicio != null, "O início da sessão é obrigatório.");
        exigir(fim != null, "O fim da sessão é obrigatório.");
        exigir(fim.isAfter(inicio), "O fim da sessão deve ser depois do início.");
    }

    private static String normalizarEndereco(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            return null;
        }
        return endereco.strip();
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new SessaoInvalidaException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public Long eventoId() {
        return eventoId;
    }

    public Long localId() {
        return localId;
    }

    public String endereco() {
        return endereco;
    }

    public LocalDateTime inicio() {
        return inicio;
    }

    public LocalDateTime fim() {
        return fim;
    }

    public StatusSessao status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sessao outra)) {
            return false;
        }
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
