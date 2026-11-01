package com.chezticket.vendas.reserva.application;

import com.chezticket.vendas.reserva.application.port.in.CancelarReservaUseCase;
import com.chezticket.vendas.reserva.application.port.in.ConsultarReservasUseCase;
import com.chezticket.vendas.reserva.application.port.in.ExpirarReservasVencidasUseCase;
import com.chezticket.vendas.reserva.application.port.in.ItemReservaComando;
import com.chezticket.vendas.reserva.application.port.in.NovaReserva;
import com.chezticket.vendas.reserva.application.port.in.ReservarIngressosUseCase;
import com.chezticket.vendas.reserva.application.port.out.GerenciarEstoqueDeIngressosPort;
import com.chezticket.vendas.reserva.application.port.out.ReservaRepositorio;
import com.chezticket.vendas.reserva.application.port.out.VerificarParticipantePort;
import com.chezticket.vendas.reserva.domain.ItemReserva;
import com.chezticket.vendas.reserva.domain.Reserva;
import com.chezticket.vendas.reserva.domain.exception.ReservaInvalidaException;
import com.chezticket.vendas.reserva.domain.exception.ReservaNaoEncontradaException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservaService implements
        ReservarIngressosUseCase,
        ConsultarReservasUseCase,
        CancelarReservaUseCase,
        ExpirarReservasVencidasUseCase {

    /** Tempo que o participante tem para concluir a compra antes do estoque voltar a ficar livre. */
    static final Duration DURACAO_DA_RESERVA = Duration.ofMinutes(10);

    private final ReservaRepositorio repositorio;
    private final VerificarParticipantePort verificarParticipante;
    private final GerenciarEstoqueDeIngressosPort estoque;

    public ReservaService(ReservaRepositorio repositorio, VerificarParticipantePort verificarParticipante,
            GerenciarEstoqueDeIngressosPort estoque) {
        this.repositorio = repositorio;
        this.verificarParticipante = verificarParticipante;
        this.estoque = estoque;
    }

    @Override
    public Reserva reservar(NovaReserva comando) {
        if (!verificarParticipante.participanteExiste(comando.participanteId())) {
            throw new ReservaInvalidaException("Participante inexistente: " + comando.participanteId() + ".");
        }
        if (comando.itens() == null || comando.itens().isEmpty()) {
            throw new ReservaInvalidaException("A reserva deve ter ao menos um item.");
        }

        List<ItemReserva> itens = comando.itens().stream()
                .map(item -> new ItemReserva(item.tipoIngressoId(), item.quantidade()))
                .toList();
        for (ItemReserva item : itens) {
            estoque.reservarUnidades(item.tipoIngressoId(), item.quantidade());
        }

        Reserva reserva = Reserva.criar(comando.participanteId(), itens, DURACAO_DA_RESERVA);
        return repositorio.salvar(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorParticipante(Long participanteId) {
        return repositorio.listarPorParticipante(participanteId);
    }

    @Override
    public Reserva cancelar(Long id) {
        Reserva reserva = carregar(id);
        reserva.cancelar();
        liberarEstoqueDaReserva(reserva);
        return repositorio.salvar(reserva);
    }

    @Override
    public int expirarVencidas() {
        List<Reserva> vencidas = repositorio.listarAtivasExpiradasAte(LocalDateTime.now());
        for (Reserva reserva : vencidas) {
            reserva.expirar();
            liberarEstoqueDaReserva(reserva);
            repositorio.salvar(reserva);
        }
        return vencidas.size();
    }

    private void liberarEstoqueDaReserva(Reserva reserva) {
        for (ItemReserva item : reserva.itens()) {
            estoque.liberarUnidades(item.tipoIngressoId(), item.quantidade());
        }
    }

    private Reserva carregar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ReservaNaoEncontradaException(id));
    }
}
