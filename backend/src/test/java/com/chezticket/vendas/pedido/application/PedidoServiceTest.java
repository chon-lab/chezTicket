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
import com.chezticket.vendas.pagamento.application.port.out.ReembolsoRepositorio;
import com.chezticket.vendas.pagamento.domain.MetodoPagamento;
import com.chezticket.vendas.pagamento.domain.MotivoReembolso;
import com.chezticket.vendas.pagamento.domain.Pagamento;
import com.chezticket.vendas.pagamento.domain.Reembolso;
import com.chezticket.vendas.pagamento.domain.TipoReembolso;
import com.chezticket.vendas.pagamento.domain.exception.PagamentoNaoEncontradoException;
import com.chezticket.vendas.pedido.application.port.in.ConfirmarPagamentoUseCase;
import com.chezticket.vendas.pedido.application.port.in.NovoPedido;
import com.chezticket.vendas.pedido.application.port.in.ResultadoPedido;
import com.chezticket.vendas.pedido.application.port.out.ConsultarEmailDoParticipantePort;
import com.chezticket.vendas.pedido.application.port.out.ConsultarTipoIngressoPort;
import com.chezticket.vendas.pedido.application.port.out.CriarSessaoDeCheckoutPort;
import com.chezticket.vendas.pedido.application.port.out.EnviarIngressosPorEmailPort;
import com.chezticket.vendas.pedido.application.port.out.ItemEmailIngresso;
import com.chezticket.vendas.pedido.application.port.out.PedidoRepositorio;
import com.chezticket.vendas.pedido.application.port.out.ProcessarReembolsoNoGatewayPort;
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
    ReembolsoRepositorio reembolsos;

    @Mock
    ReservaParaPedidoPort reservas;

    @Mock
    ConsultarTipoIngressoPort tiposIngresso;

    @Mock
    CriarSessaoDeCheckoutPort criarCheckout;

    @Mock
    ProcessarReembolsoNoGatewayPort processarReembolsoNoGateway;

    @Mock
    ConsultarEmailDoParticipantePort emailDoParticipante;

    @Mock
    EnviarIngressosPorEmailPort enviarIngressosPorEmail;

    @Mock
    ValidarCupomUseCase validarCupom;

    PedidoService service;

    @BeforeEach
    void setUp() {
        service = new PedidoService(pedidos, ingressos, pagamentos, reembolsos, reservas, tiposIngresso,
                criarCheckout, processarReembolsoNoGateway, emailDoParticipante, enviarIngressosPorEmail,
                validarCupom);
    }

    @Test
    void deveConcluirNaHoraQuandoPedidoEGratuito() {
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> comId((Pedido) inv.getArgument(0), 900L));
        Reserva reserva = reservaAtiva(10L, 4L, new ItemReserva(1L, 2));
        when(reservas.buscar(10L)).thenReturn(reserva);
        when(tiposIngresso.buscarPorId(1L)).thenReturn(tipoIngresso(1L, true, BigDecimal.ZERO, BigDecimal.ZERO));
        when(ingressos.salvar(any(Ingresso.class))).thenAnswer(inv -> comId((Ingresso) inv.getArgument(0), 1L));
        when(emailDoParticipante.buscarEmail(4L)).thenReturn("comprador@teste.dev");

        ResultadoPedido resultado = service.criar(new NovoPedido(4L, 10L, null, null));

        assertThat(resultado.checkoutUrl()).isNull();
        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.ingressos()).hasSize(2);
        verify(reservas).converter(10L);
        verify(reservas, never()).estenderExpiracao(any(), any());
        verify(criarCheckout, never()).criar(any());
        verify(pagamentos, never()).salvar(any());

        ArgumentCaptor<List<ItemEmailIngresso>> itensCaptor = ArgumentCaptor.forClass(List.class);
        verify(enviarIngressosPorEmail).enviar(eq("comprador@teste.dev"), eq(900L), itensCaptor.capture());
        assertThat(itensCaptor.getValue()).hasSize(2);
        assertThat(itensCaptor.getValue().get(0).nomeDoTipo()).isEqualTo("Inteira");
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
        verify(reservas).estenderExpiracao(eq(10L), any(LocalDateTime.class));
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
        when(tiposIngresso.buscarPorId(1L))
                .thenReturn(tipoIngresso(1L, false, new BigDecimal("50.00"), new BigDecimal("5.00")));
        when(emailDoParticipante.buscarEmail(4L)).thenReturn("comprador@teste.dev");

        ResultadoPedido resultado = service.confirmarPagamento(900L, "pi_123", MetodoPagamento.CARTAO_CREDITO);

        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.ingressos()).hasSize(2);
        verify(reservas).converter(10L);
        verify(enviarIngressosPorEmail).enviar(eq("comprador@teste.dev"), eq(900L), any());

        ArgumentCaptor<Pagamento> captor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salvar(captor.capture());
        assertThat(captor.getValue().idTransacao()).isEqualTo("pi_123");
    }

    @Test
    void deveEmitirIngressosMesmoQuandoOEnvioDeEmailFalha() {
        Pedido pendente = pedidoPendente(900L, 10L, new com.chezticket.vendas.pedido.domain.ItemPedido(1L, 2,
                new BigDecimal("50.00")));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(pendente));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ingressos.salvar(any(Ingresso.class))).thenAnswer(inv -> comId((Ingresso) inv.getArgument(0), 1L));
        when(emailDoParticipante.buscarEmail(4L)).thenThrow(new RuntimeException("SMTP fora do ar"));

        ResultadoPedido resultado = service.confirmarPagamento(900L, "pi_123", MetodoPagamento.CARTAO_CREDITO);

        assertThat(resultado.pedido().status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.ingressos()).hasSize(2);
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

    @Test
    void deveListarIngressosDoPedido() {
        Ingresso emitido = comId(Ingresso.emitir(900L, 1L, null), 1L);
        when(ingressos.listarPorPedido(900L)).thenReturn(List.of(emitido));

        List<Ingresso> resultado = service.listarIngressosDoPedido(900L);

        assertThat(resultado).containsExactly(emitido);
    }

    @Test
    void deveReembolsarTotalmenteUmPedidoConcluidoEEstornarNoGateway() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        Pagamento pagamento = pagamentoAprovado(5L, 900L, "pi_999", new BigDecimal("110.00"));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(pagamentos.buscarPorPedidoId(900L)).thenReturn(Optional.of(pagamento));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido reembolsado = service.solicitarReembolso(900L, 4L, MotivoReembolso.DESISTENCIA, TipoReembolso.TOTAL,
                null);

        assertThat(reembolsado.status()).isEqualTo(StatusPedido.REEMBOLSADO);
        verify(processarReembolsoNoGateway).reembolsar("pi_999", new BigDecimal("110.00"));

        ArgumentCaptor<Reembolso> reembolsoCaptor = ArgumentCaptor.forClass(Reembolso.class);
        verify(reembolsos).salvar(reembolsoCaptor.capture());
        assertThat(reembolsoCaptor.getValue().valor()).isEqualByComparingTo("110.00");
        assertThat(reembolsoCaptor.getValue().status().name()).isEqualTo("PROCESSADO");

        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salvar(pagamentoCaptor.capture());
        assertThat(pagamentoCaptor.getValue().status().name()).isEqualTo("ESTORNADO");
    }

    @Test
    void deveCancelarOsIngressosEmitidosAoReembolsarTotalmente() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        Pagamento pagamento = pagamentoAprovado(5L, 900L, "pi_999", new BigDecimal("110.00"));
        Ingresso emitido = comId(Ingresso.emitir(900L, 1L, null), 1L);
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(pagamentos.buscarPorPedidoId(900L)).thenReturn(Optional.of(pagamento));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ingressos.listarPorPedido(900L)).thenReturn(List.of(emitido));
        when(ingressos.salvar(any(Ingresso.class))).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarReembolso(900L, 4L, MotivoReembolso.DESISTENCIA, TipoReembolso.TOTAL, null);

        ArgumentCaptor<Ingresso> ingressoCaptor = ArgumentCaptor.forClass(Ingresso.class);
        verify(ingressos).salvar(ingressoCaptor.capture());
        assertThat(ingressoCaptor.getValue().status().name()).isEqualTo("CANCELADO");
    }

    @Test
    void deveReembolsarParcialmenteRespeitandoOValorInformado() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        Pagamento pagamento = pagamentoAprovado(5L, 900L, "pi_999", new BigDecimal("110.00"));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(pagamentos.buscarPorPedidoId(900L)).thenReturn(Optional.of(pagamento));
        when(pedidos.salvar(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarReembolso(900L, 4L, MotivoReembolso.REMARCACAO, TipoReembolso.PARCIAL,
                new BigDecimal("30.00"));

        verify(processarReembolsoNoGateway).reembolsar("pi_999", new BigDecimal("30.00"));
        verify(ingressos, never()).listarPorPedido(any());
    }

    @Test
    void naoDeveReembolsarParcialAcimaDoValorPago() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        Pagamento pagamento = pagamentoAprovado(5L, 900L, "pi_999", new BigDecimal("110.00"));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(pagamentos.buscarPorPedidoId(900L)).thenReturn(Optional.of(pagamento));

        assertThatThrownBy(() -> service.solicitarReembolso(900L, 4L, MotivoReembolso.REMARCACAO,
                TipoReembolso.PARCIAL, new BigDecimal("200.00")))
                .isInstanceOf(PedidoInvalidoException.class);

        verify(processarReembolsoNoGateway, never()).reembolsar(any(), any());
        verify(reembolsos, never()).salvar(any());
    }

    @Test
    void naoDeveReembolsarPedidoDeOutroParticipante() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));

        assertThatThrownBy(() -> service.solicitarReembolso(900L, 999L, MotivoReembolso.DESISTENCIA,
                TipoReembolso.TOTAL, null))
                .isInstanceOf(PedidoInvalidoException.class);

        verify(pagamentos, never()).buscarPorPedidoId(any());
        verify(processarReembolsoNoGateway, never()).reembolsar(any(), any());
    }

    @Test
    void naoDeveReembolsarPedidoAindaAguardandoPagamento() {
        Pedido pendente = pedidoPendente(900L, 10L,
                new com.chezticket.vendas.pedido.domain.ItemPedido(1L, 2, new BigDecimal("50.00")));
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(pendente));

        assertThatThrownBy(() -> service.solicitarReembolso(900L, 4L, MotivoReembolso.DESISTENCIA,
                TipoReembolso.TOTAL, null))
                .isInstanceOf(com.chezticket.vendas.pedido.domain.exception.TransicaoDeStatusDoPedidoInvalidaException.class);

        verify(pagamentos, never()).buscarPorPedidoId(any());
        verify(processarReembolsoNoGateway, never()).reembolsar(any(), any());
    }

    @Test
    void naoDeveReembolsarSemPagamentoRegistrado() {
        Pedido concluido = pedidoConcluido(900L, 10L);
        when(pedidos.buscarPorId(900L)).thenReturn(Optional.of(concluido));
        when(pagamentos.buscarPorPedidoId(900L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.solicitarReembolso(900L, 4L, MotivoReembolso.DESISTENCIA,
                TipoReembolso.TOTAL, null))
                .isInstanceOf(PagamentoNaoEncontradoException.class);

        verify(processarReembolsoNoGateway, never()).reembolsar(any(), any());
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

    private static Pagamento pagamentoAprovado(Long id, Long pedidoId, String idTransacao, BigDecimal valor) {
        Pagamento aprovado = Pagamento.aprovar(pedidoId, idTransacao, MetodoPagamento.CARTAO_CREDITO, valor);
        return Pagamento.reconstituir(id, aprovado.pedidoId(), aprovado.idTransacao(), aprovado.metodo(),
                aprovado.parcelas(), aprovado.valor(), aprovado.status(), aprovado.criadoEm());
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
