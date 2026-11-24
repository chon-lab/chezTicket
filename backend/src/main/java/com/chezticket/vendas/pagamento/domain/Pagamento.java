package com.chezticket.vendas.pagamento.domain;

import com.chezticket.vendas.pagamento.domain.exception.PagamentoInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Pagamento de um {@code Pedido}, feito via Stripe. Modelo de domínio puro.
 *
 * <p>Só é criado quando o Stripe confirma a cobrança (webhook {@code checkout.session.completed})
 * — tentativas malsucedidas não geram uma linha aqui, só mudam o status do {@code Pedido}
 * diretamente (ver {@code PedidoService}).
 */
public class Pagamento {

    private static final String GATEWAY_STRIPE = "STRIPE";

    private Long id;
    private final Long pedidoId;
    private final String idTransacao;
    private final MetodoPagamento metodo;
    private final int parcelas;
    private final BigDecimal valor;
    private StatusPagamento status;
    private final LocalDateTime criadoEm;

    private Pagamento(Long id, Long pedidoId, String idTransacao, MetodoPagamento metodo, int parcelas,
            BigDecimal valor, StatusPagamento status, LocalDateTime criadoEm) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.idTransacao = idTransacao;
        this.metodo = metodo;
        this.parcelas = parcelas;
        this.valor = valor;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    /** Registra um pagamento já aprovado pelo Stripe. */
    public static Pagamento aprovar(Long pedidoId, String idTransacao, MetodoPagamento metodo, BigDecimal valor) {
        Objects.requireNonNull(pedidoId, "pedidoId");
        if (idTransacao == null || idTransacao.isBlank()) {
            throw new PagamentoInvalidoException("O id da transação é obrigatório.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new PagamentoInvalidoException("O valor do pagamento não pode ser negativo.");
        }
        return new Pagamento(null, pedidoId, idTransacao, metodo, 1, valor, StatusPagamento.APROVADO,
                LocalDateTime.now());
    }

    /** Reconstrói um pagamento já existente. Usado pelo adaptador de persistência. */
    public static Pagamento reconstituir(Long id, Long pedidoId, String idTransacao, MetodoPagamento metodo,
            int parcelas, BigDecimal valor, StatusPagamento status, LocalDateTime criadoEm) {
        return new Pagamento(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(pedidoId, "pedidoId"),
                idTransacao,
                metodo,
                parcelas,
                valor,
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(criadoEm, "criadoEm"));
    }

    public void marcarEstornado() {
        this.status = StatusPagamento.ESTORNADO;
    }

    public String gateway() {
        return GATEWAY_STRIPE;
    }

    public Long id() {
        return id;
    }

    public Long pedidoId() {
        return pedidoId;
    }

    public String idTransacao() {
        return idTransacao;
    }

    public MetodoPagamento metodo() {
        return metodo;
    }

    public int parcelas() {
        return parcelas;
    }

    public BigDecimal valor() {
        return valor;
    }

    public StatusPagamento status() {
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
        if (!(o instanceof Pagamento outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
