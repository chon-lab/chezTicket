package com.chezticket.vendas.pedido.application;

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
import com.chezticket.vendas.pedido.application.port.in.CancelarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.ConfirmarPagamentoUseCase;
import com.chezticket.vendas.pedido.application.port.in.ConsultarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.CriarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.NovoPedido;
import com.chezticket.vendas.pedido.application.port.in.ResultadoPedido;
import com.chezticket.vendas.pedido.application.port.in.SolicitarReembolsoUseCase;
import com.chezticket.vendas.pedido.application.port.out.ConsultarEmailDoParticipantePort;
import com.chezticket.vendas.pedido.application.port.out.ConsultarTipoIngressoPort;
import com.chezticket.vendas.pedido.application.port.out.CriarSessaoDeCheckoutPort;
import com.chezticket.vendas.pedido.application.port.out.EnviarIngressosPorEmailPort;
import com.chezticket.vendas.pedido.application.port.out.ItemDeCheckout;
import com.chezticket.vendas.pedido.application.port.out.ItemEmailIngresso;
import com.chezticket.vendas.pedido.application.port.out.PedidoRepositorio;
import com.chezticket.vendas.pedido.application.port.out.ProcessarReembolsoNoGatewayPort;
import com.chezticket.vendas.pedido.application.port.out.ReservaParaPedidoPort;
import com.chezticket.vendas.pedido.application.port.out.SolicitacaoDeCheckout;
import com.chezticket.vendas.pedido.domain.ItemPedido;
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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PedidoService implements
        CriarPedidoUseCase,
        ConsultarPedidoUseCase,
        ConfirmarPagamentoUseCase,
        CancelarPedidoUseCase,
        SolicitarReembolsoUseCase {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    /**
     * Quanto tempo o pagamento pode ficar em aberto no Stripe Checkout. Maior que o prazo
     * padrão de retenção da reserva (10 min) — a reserva é adiada para esta mesma janela ao
     * abrir o checkout, senão o agendador de expiração correria com o pagamento ainda em
     * andamento (ver {@code Reserva.estenderExpiracao}).
     */
    static final Duration JANELA_DE_PAGAMENTO = Duration.ofMinutes(35);

    private final PedidoRepositorio pedidos;
    private final IngressoRepositorio ingressos;
    private final PagamentoRepositorio pagamentos;
    private final ReembolsoRepositorio reembolsos;
    private final ReservaParaPedidoPort reservas;
    private final ConsultarTipoIngressoPort tiposIngresso;
    private final CriarSessaoDeCheckoutPort criarCheckout;
    private final ProcessarReembolsoNoGatewayPort processarReembolsoNoGateway;
    private final ConsultarEmailDoParticipantePort emailDoParticipante;
    private final EnviarIngressosPorEmailPort enviarIngressosPorEmail;
    private final ValidarCupomUseCase validarCupom;

    public PedidoService(PedidoRepositorio pedidos, IngressoRepositorio ingressos, PagamentoRepositorio pagamentos,
            ReembolsoRepositorio reembolsos, ReservaParaPedidoPort reservas, ConsultarTipoIngressoPort tiposIngresso,
            CriarSessaoDeCheckoutPort criarCheckout, ProcessarReembolsoNoGatewayPort processarReembolsoNoGateway,
            ConsultarEmailDoParticipantePort emailDoParticipante, EnviarIngressosPorEmailPort enviarIngressosPorEmail,
            ValidarCupomUseCase validarCupom) {
        this.pedidos = pedidos;
        this.ingressos = ingressos;
        this.pagamentos = pagamentos;
        this.reembolsos = reembolsos;
        this.reservas = reservas;
        this.tiposIngresso = tiposIngresso;
        this.criarCheckout = criarCheckout;
        this.processarReembolsoNoGateway = processarReembolsoNoGateway;
        this.emailDoParticipante = emailDoParticipante;
        this.enviarIngressosPorEmail = enviarIngressosPorEmail;
        this.validarCupom = validarCupom;
    }

    @Override
    public ResultadoPedido criar(NovoPedido comando) {
        Reserva reserva = reservas.buscar(comando.reservaId());
        if (!reserva.participanteId().equals(comando.participanteId())) {
            throw new PedidoInvalidoException("A reserva não pertence ao participante.");
        }
        if (reserva.status() != StatusReserva.ATIVA) {
            throw new PedidoInvalidoException("A reserva não está mais ativa.");
        }

        List<ItemPedido> itensPedido = new ArrayList<>();
        List<ItemDeCheckout> itensCheckout = new ArrayList<>();
        BigDecimal valorTaxa = BigDecimal.ZERO;
        for (ItemReserva item : reserva.itens()) {
            TipoIngresso tipo = tiposIngresso.buscarPorId(item.tipoIngressoId());
            itensPedido.add(new ItemPedido(tipo.id(), item.quantidade(), tipo.preco()));
            itensCheckout.add(new ItemDeCheckout(tipo.nome(), tipo.preco(), item.quantidade()));
            if (!tipo.gratuito()) {
                valorTaxa = valorTaxa.add(tipo.taxaServico().multiply(BigDecimal.valueOf(item.quantidade())));
            }
        }

        Long cupomId = null;
        int percentualDesconto = 0;
        if (comando.codigoCupom() != null && !comando.codigoCupom().isBlank()) {
            Cupom cupom = validarCupom.validar(comando.eventoId(), comando.codigoCupom());
            cupomId = cupom.id();
            percentualDesconto = cupom.percentualDesconto();
        }

        Pedido pedido = Pedido.criar(comando.participanteId(), reserva.id(), cupomId, itensPedido, valorTaxa,
                percentualDesconto);
        pedido = pedidos.salvar(pedido);

        if (pedido.estaQuitado()) {
            return concluirSemCobranca(pedido);
        }

        reservas.estenderExpiracao(reserva.id(), LocalDateTime.now().plus(JANELA_DE_PAGAMENTO));
        String checkoutUrl = criarCheckout.criar(
                new SolicitacaoDeCheckout(pedido.id(), itensCheckout, valorTaxa, JANELA_DE_PAGAMENTO));
        return new ResultadoPedido(pedido, checkoutUrl, List.of());
    }

    private ResultadoPedido concluirSemCobranca(Pedido pedido) {
        pedido.confirmarPagamento();
        pedido.concluir();
        pedido = pedidos.salvar(pedido);
        List<Ingresso> emitidos = emitirIngressos(pedido);
        reservas.converter(pedido.reservaId());
        return new ResultadoPedido(pedido, null, emitidos);
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorParticipante(Long participanteId) {
        return pedidos.listarPorParticipante(participanteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosDoPedido(Long pedidoId) {
        return ingressos.listarPorPedido(pedidoId);
    }

    @Override
    public ResultadoPedido confirmarPagamento(Long pedidoId, String idTransacaoStripe, MetodoPagamento metodo) {
        Pedido pedido = carregar(pedidoId);
        if (pedido.status() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            return new ResultadoPedido(pedido, null, ingressos.listarPorPedido(pedido.id()));
        }

        pedido.confirmarPagamento();
        pedido.concluir();
        pedido = pedidos.salvar(pedido);

        pagamentos.salvar(Pagamento.aprovar(pedido.id(), idTransacaoStripe, metodo, pedido.valorTotal()));
        List<Ingresso> emitidos = emitirIngressos(pedido);
        if (pedido.reservaId() != null) {
            reservas.converter(pedido.reservaId());
        }
        return new ResultadoPedido(pedido, null, emitidos);
    }

    @Override
    public Pedido recusar(Long pedidoId) {
        return cancelarPendente(pedidoId, false);
    }

    @Override
    public Pedido expirar(Long pedidoId) {
        return cancelarPendente(pedidoId, true);
    }

    private Pedido cancelarPendente(Long pedidoId, boolean porExpiracao) {
        Pedido pedido = carregar(pedidoId);
        if (pedido.status() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            return pedido;
        }
        if (porExpiracao) {
            pedido.expirar();
        } else {
            pedido.recusar();
        }
        pedido = pedidos.salvar(pedido);
        if (pedido.reservaId() != null) {
            reservas.cancelar(pedido.reservaId());
        }
        return pedido;
    }

    @Override
    public Pedido solicitarReembolso(Long pedidoId, Long participanteId, MotivoReembolso motivo, TipoReembolso tipo,
            BigDecimal valor) {
        Pedido pedido = carregar(pedidoId);
        if (!pedido.participanteId().equals(participanteId)) {
            throw new PedidoInvalidoException("O pedido não pertence ao participante.");
        }
        // Valida a transição de status (só PAGO/CONCLUIDO podem ser reembolsados) antes de
        // chamar o gateway ou persistir qualquer coisa — falhar aqui não deve ter efeito colateral.
        pedido.marcarReembolsado();

        Pagamento pagamento = pagamentos.buscarPorPedidoId(pedidoId)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(pedidoId));

        BigDecimal valorAEstornar = tipo == TipoReembolso.TOTAL ? pagamento.valor() : valor;
        if (tipo == TipoReembolso.PARCIAL
                && (valorAEstornar == null || valorAEstornar.compareTo(pagamento.valor()) > 0)) {
            throw new PedidoInvalidoException(
                    "O valor do reembolso parcial não pode exceder o valor pago (" + pagamento.valor() + ").");
        }

        processarReembolsoNoGateway.reembolsar(pagamento.idTransacao(), valorAEstornar);

        Reembolso reembolso = Reembolso.solicitar(pagamento.id(), motivo, tipo, valorAEstornar);
        reembolso.processar();
        reembolsos.salvar(reembolso);

        pagamento.marcarEstornado();
        pagamentos.salvar(pagamento);

        if (tipo == TipoReembolso.TOTAL) {
            cancelarIngressos(pedidoId);
        }

        return pedidos.salvar(pedido);
    }

    private void cancelarIngressos(Long pedidoId) {
        for (Ingresso ingresso : ingressos.listarPorPedido(pedidoId)) {
            ingresso.cancelar();
            ingressos.salvar(ingresso);
        }
    }

    private List<Ingresso> emitirIngressos(Pedido pedido) {
        List<Ingresso> emitidos = new ArrayList<>();
        for (ItemPedido item : pedido.itens()) {
            for (int i = 0; i < item.quantidade(); i++) {
                emitidos.add(ingressos.salvar(Ingresso.emitir(pedido.id(), item.tipoIngressoId(), null)));
            }
        }
        enviarEmailDeConfirmacao(pedido, emitidos);
        return emitidos;
    }

    /**
     * Best-effort: qualquer falha (participante sem e-mail resolvível, SMTP fora do ar, etc.)
     * só vira um aviso no log — o ingresso já foi emitido e a compra não deve falhar por causa
     * da notificação.
     */
    private void enviarEmailDeConfirmacao(Pedido pedido, List<Ingresso> emitidos) {
        try {
            String destinatario = emailDoParticipante.buscarEmail(pedido.participanteId());
            List<ItemEmailIngresso> itens = emitidos.stream()
                    .map(ingresso -> new ItemEmailIngresso(
                            tiposIngresso.buscarPorId(ingresso.tipoIngressoId()).nome(),
                            ingresso.codigoQr()))
                    .toList();
            enviarIngressosPorEmail.enviar(destinatario, pedido.id(), itens);
        } catch (Exception e) {
            log.warn("Não foi possível enviar o e-mail de confirmação do pedido {}: {}", pedido.id(),
                    e.getMessage());
        }
    }

    private Pedido carregar(Long id) {
        return pedidos.buscarPorId(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }
}
