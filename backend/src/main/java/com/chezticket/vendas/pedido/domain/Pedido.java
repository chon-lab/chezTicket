package com.chezticket.vendas.pedido.domain;

import com.chezticket.vendas.pedido.domain.exception.PedidoInvalidoException;
import com.chezticket.vendas.pedido.domain.exception.TransicaoDeStatusDoPedidoInvalidaException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Pedido: converte uma {@code Reserva} em uma compra com valor. Modelo de domínio puro.
 *
 * <p>Concentra o cálculo de totais (Figura 3: {@code calcularTotais()}) e as transições de
 * status: {@code AGUARDANDO_PAGAMENTO} → {@code PAGO} → {@code CONCLUIDO} (depois que os
 * ingressos são emitidos), com {@code RECUSADO}/{@code EXPIRADO} a partir da espera, e
 * {@code REEMBOLSADO} a partir de um pedido pago.
 */
public class Pedido {

    private Long id;
    private final Long participanteId;
    private final Long reservaId;
    private final Long cupomId;
    private final List<ItemPedido> itens;
    private final BigDecimal subtotal;
    private final BigDecimal valorTaxa;
    private final BigDecimal valorTotal;
    private StatusPedido status;
    private final LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    private Pedido(Long id, Long participanteId, Long reservaId, Long cupomId, List<ItemPedido> itens,
            BigDecimal subtotal, BigDecimal valorTaxa, BigDecimal valorTotal, StatusPedido status,
            LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.participanteId = participanteId;
        this.reservaId = reservaId;
        this.cupomId = cupomId;
        this.itens = itens;
        this.subtotal = subtotal;
        this.valorTaxa = valorTaxa;
        this.valorTotal = valorTotal;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    /**
     * Cria um pedido novo, calculando os totais (Figura 3). O desconto do cupom — se houver —
     * incide só sobre o subtotal dos ingressos, nunca sobre a taxa de serviço.
     */
    public static Pedido criar(Long participanteId, Long reservaId, Long cupomId, List<ItemPedido> itens,
            BigDecimal valorTaxa, int percentualDesconto) {
        exigir(participanteId != null, "O participante do pedido é obrigatório.");
        exigir(itens != null && !itens.isEmpty(), "O pedido deve ter ao menos um item.");
        exigir(percentualDesconto >= 0 && percentualDesconto <= 100, "Desconto percentual inválido.");
        BigDecimal taxa = valorTaxa == null ? BigDecimal.ZERO : valorTaxa;
        exigir(taxa.compareTo(BigDecimal.ZERO) >= 0, "A taxa de serviço não pode ser negativa.");

        BigDecimal subtotal = itens.stream()
                .map(ItemPedido::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal desconto = subtotal
                .multiply(BigDecimal.valueOf(percentualDesconto))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(desconto).add(taxa);

        LocalDateTime agora = LocalDateTime.now();
        return new Pedido(null, participanteId, reservaId, cupomId, List.copyOf(itens), subtotal, taxa, total,
                StatusPedido.AGUARDANDO_PAGAMENTO, agora, agora);
    }

    /** Reconstrói um pedido já existente. Usado pelo adaptador de persistência. */
    public static Pedido reconstituir(Long id, Long participanteId, Long reservaId, Long cupomId,
            List<ItemPedido> itens, BigDecimal subtotal, BigDecimal valorTaxa, BigDecimal valorTotal,
            StatusPedido status, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new Pedido(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(participanteId, "participanteId"),
                reservaId,
                cupomId,
                List.copyOf(itens),
                subtotal,
                valorTaxa,
                valorTotal,
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(criadoEm, "criadoEm"),
                atualizadoEm);
    }

    /** Verdadeiro quando o total é zero — todos os ingressos são gratuitos, sem Stripe envolvido. */
    public boolean estaQuitado() {
        return valorTotal.compareTo(BigDecimal.ZERO) == 0;
    }

    public void confirmarPagamento() {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new TransicaoDeStatusDoPedidoInvalidaException(status, StatusPedido.PAGO);
        }
        transicionarPara(StatusPedido.PAGO);
    }

    public void concluir() {
        if (status != StatusPedido.PAGO) {
            throw new TransicaoDeStatusDoPedidoInvalidaException(status, StatusPedido.CONCLUIDO);
        }
        transicionarPara(StatusPedido.CONCLUIDO);
    }

    public void recusar() {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new TransicaoDeStatusDoPedidoInvalidaException(status, StatusPedido.RECUSADO);
        }
        transicionarPara(StatusPedido.RECUSADO);
    }

    public void expirar() {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new TransicaoDeStatusDoPedidoInvalidaException(status, StatusPedido.EXPIRADO);
        }
        transicionarPara(StatusPedido.EXPIRADO);
    }

    public void marcarReembolsado() {
        if (status != StatusPedido.PAGO && status != StatusPedido.CONCLUIDO) {
            throw new TransicaoDeStatusDoPedidoInvalidaException(status, StatusPedido.REEMBOLSADO);
        }
        transicionarPara(StatusPedido.REEMBOLSADO);
    }

    private void transicionarPara(StatusPedido novoStatus) {
        this.status = novoStatus;
        this.atualizadoEm = LocalDateTime.now();
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new PedidoInvalidoException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public Long participanteId() {
        return participanteId;
    }

    public Long reservaId() {
        return reservaId;
    }

    public Long cupomId() {
        return cupomId;
    }

    public List<ItemPedido> itens() {
        return itens;
    }

    public BigDecimal subtotal() {
        return subtotal;
    }

    public BigDecimal valorTaxa() {
        return valorTaxa;
    }

    public BigDecimal valorTotal() {
        return valorTotal;
    }

    public StatusPedido status() {
        return status;
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
        if (!(o instanceof Pedido outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
