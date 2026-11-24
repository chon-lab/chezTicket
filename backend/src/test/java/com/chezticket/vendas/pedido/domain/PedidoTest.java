package com.chezticket.vendas.pedido.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.vendas.pedido.domain.exception.PedidoInvalidoException;
import com.chezticket.vendas.pedido.domain.exception.TransicaoDeStatusDoPedidoInvalidaException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PedidoTest {

    private final List<ItemPedido> doisItensDe50 = List.of(new ItemPedido(1L, 2, new BigDecimal("50.00")));

    @Test
    void deveCalcularSubtotalTaxaETotalSemCupom() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, new BigDecimal("10.00"), 0);

        assertThat(pedido.subtotal()).isEqualByComparingTo("100.00");
        assertThat(pedido.valorTaxa()).isEqualByComparingTo("10.00");
        assertThat(pedido.valorTotal()).isEqualByComparingTo("110.00");
        assertThat(pedido.status()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
    }

    @Test
    void descontoDoCupomIncideSoSobreOSubtotalNuncaSobreATaxa() {
        Pedido pedido = Pedido.criar(4L, 10L, 7L, doisItensDe50, new BigDecimal("10.00"), 10);

        assertThat(pedido.subtotal()).isEqualByComparingTo("100.00");
        assertThat(pedido.valorTotal()).isEqualByComparingTo("100.00"); // 100 - 10% + 10 de taxa = 100
    }

    @Test
    void deveEstarQuitadoQuandoTotalEZero() {
        Pedido gratuito = Pedido.criar(4L, 10L, null, List.of(new ItemPedido(1L, 2, BigDecimal.ZERO)),
                BigDecimal.ZERO, 0);

        assertThat(gratuito.estaQuitado()).isTrue();
    }

    @Test
    void naoDeveEstarQuitadoQuandoHaValorAPagar() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);

        assertThat(pedido.estaQuitado()).isFalse();
    }

    @Test
    void naoDeveCriarSemItens() {
        assertThatThrownBy(() -> Pedido.criar(4L, 10L, null, List.of(), BigDecimal.ZERO, 0))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void naoDeveCriarComDescontoForaDoIntervalo() {
        assertThatThrownBy(() -> Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 101))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void devePercorrerOFluxoDePagamentoAteConcluido() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);

        pedido.confirmarPagamento();
        assertThat(pedido.status()).isEqualTo(StatusPedido.PAGO);

        pedido.concluir();
        assertThat(pedido.status()).isEqualTo(StatusPedido.CONCLUIDO);
    }

    @Test
    void naoDeveConcluirSemEstarPago() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);

        assertThatThrownBy(pedido::concluir).isInstanceOf(TransicaoDeStatusDoPedidoInvalidaException.class);
    }

    @Test
    void deveRecusarUmPedidoAguardandoPagamento() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);

        pedido.recusar();

        assertThat(pedido.status()).isEqualTo(StatusPedido.RECUSADO);
    }

    @Test
    void naoDeveRecusarDuasVezes() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);
        pedido.recusar();

        assertThatThrownBy(pedido::recusar).isInstanceOf(TransicaoDeStatusDoPedidoInvalidaException.class);
    }

    @Test
    void deveMarcarReembolsadoAPartirDePagoOuConcluido() {
        Pedido pago = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);
        pago.confirmarPagamento();

        pago.marcarReembolsado();

        assertThat(pago.status()).isEqualTo(StatusPedido.REEMBOLSADO);
    }

    @Test
    void naoDeveMarcarReembolsadoAPartirDeAguardandoPagamento() {
        Pedido pedido = Pedido.criar(4L, 10L, null, doisItensDe50, BigDecimal.ZERO, 0);

        assertThatThrownBy(pedido::marcarReembolsado).isInstanceOf(TransicaoDeStatusDoPedidoInvalidaException.class);
    }

    @Test
    void itemPedidoNaoAceitaValoresInvalidos() {
        assertThatThrownBy(() -> new ItemPedido(null, 1, BigDecimal.TEN)).isInstanceOf(PedidoInvalidoException.class);
        assertThatThrownBy(() -> new ItemPedido(1L, 0, BigDecimal.TEN)).isInstanceOf(PedidoInvalidoException.class);
        assertThatThrownBy(() -> new ItemPedido(1L, 1, new BigDecimal("-1"))).isInstanceOf(
                PedidoInvalidoException.class);
    }
}
