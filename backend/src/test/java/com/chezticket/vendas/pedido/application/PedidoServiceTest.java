package com.chezticket.vendas.pedido.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.vendas.cupom.application.port.in.ValidarCupomUseCase;
import com.chezticket.vendas.cupom.domain.Cupom;
import com.chezticket.vendas.ingresso.application.port.out.IngressoRepositorio;
import com.chezticket.vendas.ingresso.domain.Ingresso;
import com.chezticket.vendas.pagamento.application.port.out.PagamentoRepositorio;
import com.chezticket.vendas.pagamento.domain.MetodoPagamento;
import com.chezticket.vendas.pagamento.domain.Pagamento;
import com.chezticket.vendas.pedido.application.port.in.ConfirmarPagamentoUseCase;
import com.chezticket.vendas.pedido.application.port.in.NovoPedido;
import com.chezticket.vendas.pedido.application.port.in.ResultadoPedido;
import com.chezticket.vendas.pedido.application.port.out.ConsultarTipoIngressoPort;
import com.chezticket.vendas.pedido.application.port.out.CriarSessaoDeCheckoutPort;
import com.chezticket.vendas.pedido.application.port.out.PedidoRepositorio;
import com.chezticket.vendas.pedido.application.port.out.ReservaParaPedidoPort;
import com.chezticket.vendas.pedido.domain.Pedido;
import com.chezticket.vendas.pedido.domain.StatusPedido;
import com.chezticket.vendas.pedido.domain.exception.PedidoInvalidoException;
import com.chezticket.vendas.pedido.domain.exception.PedidoNaoEncontradoException;
import com.chezticket.vendas.reserva.domain.ItemReserva;
import com.chezticket.vendas.reserva.domain.Reserva;
import com.chezticket.vendas.reserva.domain.StatusReserva;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    PedidoRepositorio pedidos;

    @Mock
    IngressoRepositorio ingressos;

    @Mock
    PagamentoRepositorio pagamentos;

    @Mock
    ReservaParaPedidoPort reservas;

    @Mock
    ConsultarTipoIngressoPort tiposIngresso;

    @Mock
    CriarSessaoDeCheckoutPort criarCheckout;

    @Mock
    ValidarCupomUseCase validarCupom;

    PedidoService service;

    @BeforeEach
    void setUp() {
        service = new PedidoService(pedidos, ingressos, pagamentos, reservas, tiposIngresso, criarCheckout,
                validarCupom);
    }

    @Test
    void deveConcluirNaHoraQuandoPedidoEGratuito() {
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> comId((Pedido) inv.getArgument(0), 900L));
        Reserva reserva = reservaAtiva(10L, 4L, new ItemReserva(1L, 2));
        when(reservas.buscar(10L)).thenReturn(reserva);
        when(tiposIngresso.buscarPorId(1L)).thenReturn(tipoIngresso(1L, true, BigDecimal.ZERO, BigDecimal.ZERO));
        when(ingressos.salvar(any(Ingresso.class))).thenAnswer(inv -> comId((Ingresso) inv.getArgument(0), 1L));

        ResultadoPedido resultado = service.criar(new NovoPedido(4L, 10L, null, null));

        assertThat(resultado.checkoutUrl()).isNull();
        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.ingressos()).hasSize(2);
        verify(reservas).converter(10L);
        verify(criarCheckout, never()).criar(any());
        verify(pagamentos, never()).salvar(any());
    }

    @Test
    void deveAbrirCheckoutQuandoPedidoEPago() {
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> comId((Pedido) inv.getArgument(0), 900L));
        Reserva reserva = reservaAtiva(10L, 4L, new ItemReserva(1L, 2));
        when(reservas.buscar(10L)).thenReturn(reserva);
        when(tiposIngresso.buscarPorId(1L))
                .thenReturn(tipoIngresso(1L, false, new BigDecimal("50.00"), new BigDecimal("5.00")));
        when(criarCheckout.criar(any())).thenReturn("https://checkout.stripe.com/xyz");

        ResultadoPedido resultado = service.criar(new NovoPedido(4L, 10L, null, null));

        assertThat(resultado.checkoutUrl()).isEqualTo("https://checkout.stripe.com/xyz");
        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(resultado.ingressos()).isEmpty();
        assertThat(resultado.pedido().subtotal()).isEqualByComparingTo("100.00");
        assertThat(resultado.pedido().valorTaxa()).isEqualByComparingTo("10.00");
        assertThat(resultado.pedido().valorTotal()).isEqualByComparingTo("110.00");
        verify(reservas, never()).converter(any());
    }

    @Test
    void deveAplicarDescontoDoCupomSoNoSubtotal() {
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> comId((Pedido) inv.getArgument(0), 900L));
        Reserva reserva = reservaAtiva(10L, 4L, new ItemReserva(1L, 2));
        when(reservas.buscar(10L)).thenReturn(reserva);
        when(tiposIngresso.buscarPorId(1L))
                .thenReturn(tipoIngresso(1L, false, new BigDecimal("50.00"), new BigDecimal("5.00")));
        when(validarCupom.validar(99L, "PROMO2026")).thenReturn(cupom(7L, 10));
        when(criarCheckout.criar(any())).thenReturn("https://checkout.stripe.com/xyz");

        ResultadoPedido resultado = service.criar(new NovoPedido(4L, 10L, 99L, "PROMO2026"));

        assertThat(resultado.pedido().cupomId()).isEqualTo(7L);
        assertThat(resultado.pedido().subtotal()).isEqualByComparingTo("100.00");
        assertThat(resultado.pedido().valorTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    void naoDeveCriarQuandoReservaNaoPertenceAoParticipante() {
        Reserva reserva = reservaAtiva(10L, 4L, new ItemReserva(1L, 2));
        when(reservas.buscar(10L)).thenReturn(reserva);

        assertThatThrownBy(() -> service.criar(new NovoPedido(5L, 10L, null, null)))
                .isInstanceOf(PedidoInvalidoException.class);

        verify(pedidos, never()).salvar(any());
    }

    @Test
    void naoDeveCriarQuandoReservaNaoEstaAtiva() {
        Reserva reserva = Reserva.reconstituir(10L, 4L, List.of(new ItemReserva(1L, 2)),
                LocalDateTime.now().plusMinutes(10), StatusReserva.CANCELADA, LocalDateTime.now());
        when(reservas.buscar(10L)).thenReturn(reserva);

        assertThatThrownBy(() -> service.criar(new NovoPedido(4L, 10L, null, null)))
                .isInstanceOf(PedidoInvalidoException.class);

        verify(pedidos, never()).salvar(any());
    }

    @Test
    void deveConfirmarPagamentoEEmitirIngressos() {
        Pedido pendente = pedidoPendente(900L, 10L, new com.chezticket.vendas.pedido.domain.ItemPedido(1L, 2,
                new BigDecimal("50.00")));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(pendente));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ingressos.salvar(any(Ingresso.class))).thenAnswer(inv -> comId((Ingresso) inv.getArgument(0), 1L));

        ResultadoPedido resultado = service.confirmarPagamento(900L, "pi_123", MetodoPagamento.CARTAO_CREDITO);

        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.ingressos()).hasSize(2);
        verify(reservas).converter(10L);

        ArgumentCaptor<Pagamento> captor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salvar(captor.capture());
        assertThat(captor.getValue().idTransacao()).isEqualTo("pi_123");
    }

    @Test
    void deveSerIdempotentePagamentoDeConfirmarPagamento() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(ingressos.listarPorPedido(900L)).thenReturn(List.of());

        ResultadoPedido resultado = service.confirmarPagamento(900L, "pi_123", MetodoPagamento.CARTAO_CREDITO);

        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        verify(pagamentos, never()).salvar(any());
        verify(reservas, never()).converter(anyLong());
    }

    @Test
    void deveRecusarPedidoPendenteELiberarReserva() {
        Pedido pendente = pedidoPendente(900L, 10L, new com.chezticket.vendas.pedido.domain.ItemPedido(1L, 2,
                new BigDecimal("50.00")));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(pendente));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido recusado = service.recusar(900L);

        assertThat(recusado.status()).isEqualTo(StatusPedido.RECUSADO);
        verify(reservas).cancelar(10L);
    }

    @Test
    void naoDeveRecusarPedidoJaFinalizado() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));

        Pedido resultado = service.recusar(900L);

        assertThat(resultado.status()).isEqualTo(StatusPedido.CONCLUIDO);
        verify(reservas, never()).cancelar(anyLong());
        verify(pedidos, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarPedidoInexistente() {
        when(pedidos.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(404L)).isInstanceOf(PedidoNaoEncontradoException.class);
    }

    private static Reserva reservaAtiva(Long id, Long participanteId, ItemReserva... itens) {
        return Reserva.reconstituir(id, participanteId, List.of(itens), LocalDateTime.now().plusMinutes(10),
                StatusReserva.ATIVA, LocalDateTime.now());
    }

    private static TipoIngresso tipoIngresso(Long id, boolean gratuito, BigDecimal preco, BigDecimal taxaServico) {
        return TipoIngresso.reconstituir(id, 1L, "Inteira", gratuito, preco, taxaServico, 100, 0, 10, null, null, 0);
    }

    private static Cupom cupom(Long id, int percentualDesconto) {
        return Cupom.reconstituir(id, 99L, "PROMO2026", percentualDesconto, LocalDateTime.now().plusDays(1)
                .toLocalDate());
    }

    private static Pedido pedidoPendente(Long id, Long reservaId, com.chezticket.vendas.pedido.domain.ItemPedido item) {
        Pedido criado = Pedido.criar(4L, reservaId, null, List.of(item), new BigDecimal("10.00"), 0);
        return comId(criado, id);
    }

    private static Pedido pedidoConcluido(Long id, Long reservaId) {
        Pedido pendente = pedidoPendente(id, reservaId,
                new com.chezticket.vendas.pedido.domain.ItemPedido(1L, 2, new BigDecimal("50.00")));
        pendente.confirmarPagamento();
        pendente.concluir();
        return pendente;
    }

    private static Pedido comId(Pedido pedido, Long id) {
        return Pedido.reconstituir(id, pedido.participanteId(), pedido.reservaId(), pedido.cupomId(), pedido.itens(),
                pedido.subtotal(), pedido.valorTaxa(), pedido.valorTotal(), pedido.status(), pedido.criadoEm(),
                pedido.atualizadoEm());
    }

    private static Ingresso comId(Ingresso ingresso, Long id) {
        return Ingresso.reconstituir(id, ingresso.pedidoId(), ingresso.tipoIngressoId(), ingresso.codigoQr(),
                ingresso.status(), ingresso.titular(), ingresso.dataEmissao());
    }
}
