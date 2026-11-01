package com.chezticket.vendas.reserva.domain;

import com.chezticket.vendas.reserva.domain.exception.ReservaInvalidaException;
import com.chezticket.vendas.reserva.domain.exception.TransicaoDeStatusDaReservaInvalidaException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Reserva temporária de ingressos (Figura 3 da modelagem). Modelo de domínio puro.
 *
 * <p>Representa a retenção de estoque enquanto o participante conclui (ou desiste de) uma
 * compra: nasce ATIVA com um prazo de expiração e termina CONVERTIDA (em um pedido, fora do
 * escopo deste módulo), EXPIRADA ou CANCELADA — nos dois últimos casos, o estoque retido
 * volta a ficar disponível.
 */
public class Reserva {

    private Long id;
    private final Long participanteId;
    private final List<ItemReserva> itens;
    private LocalDateTime expiraEm;
    private StatusReserva status;
    private final LocalDateTime criadaEm;

    private Reserva(Long id, Long participanteId, List<ItemReserva> itens, LocalDateTime expiraEm,
            StatusReserva status, LocalDateTime criadaEm) {
        this.id = id;
        this.participanteId = participanteId;
        this.itens = itens;
        this.expiraEm = expiraEm;
        this.status = status;
        this.criadaEm = criadaEm;
    }

    /** Cria uma reserva nova, ATIVA, válida por {@code duracao} a partir de agora. */
    public static Reserva criar(Long participanteId, List<ItemReserva> itens, Duration duracao) {
        exigir(participanteId != null, "O participante da reserva é obrigatório.");
        exigir(itens != null && !itens.isEmpty(), "A reserva deve ter ao menos um item.");
        exigir(duracao != null && !duracao.isNegative() && !duracao.isZero(),
                "A duração da reserva deve ser maior que zero.");
        LocalDateTime agora = LocalDateTime.now();
        return new Reserva(null, participanteId, List.copyOf(itens), agora.plus(duracao), StatusReserva.ATIVA,
                agora);
    }

    /** Reconstrói uma reserva já existente. Usado pelo adaptador de persistência. */
    public static Reserva reconstituir(Long id, Long participanteId, List<ItemReserva> itens,
            LocalDateTime expiraEm, StatusReserva status, LocalDateTime criadaEm) {
        return new Reserva(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(participanteId, "participanteId"),
                List.copyOf(itens),
                Objects.requireNonNull(expiraEm, "expiraEm"),
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(criadaEm, "criadaEm"));
    }

    /** Verdadeiro quando a reserva ainda está ATIVA mas o prazo já passou. */
    public boolean expirada() {
        return status == StatusReserva.ATIVA && LocalDateTime.now().isAfter(expiraEm);
    }

    public void expirar() {
        if (status != StatusReserva.ATIVA) {
            throw new TransicaoDeStatusDaReservaInvalidaException(status, StatusReserva.EXPIRADA);
        }
        this.status = StatusReserva.EXPIRADA;
    }

    public void cancelar() {
        if (status != StatusReserva.ATIVA) {
            throw new TransicaoDeStatusDaReservaInvalidaException(status, StatusReserva.CANCELADA);
        }
        this.status = StatusReserva.CANCELADA;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new ReservaInvalidaException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public Long participanteId() {
        return participanteId;
    }

    public List<ItemReserva> itens() {
        return itens;
    }

    public LocalDateTime expiraEm() {
        return expiraEm;
    }

    public StatusReserva status() {
        return status;
    }

    public LocalDateTime criadaEm() {
        return criadaEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva outra)) {
            return false;
        }
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
