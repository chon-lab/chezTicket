package com.chezticket.vendas.reserva.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.vendas.reserva.application.port.in.ItemReservaComando;
import com.chezticket.vendas.reserva.application.port.in.NovaReserva;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    ReservaRepositorio repositorio;

    @Mock
    VerificarParticipantePort verificarParticipante;

    @Mock
    GerenciarEstoqueDeIngressosPort estoque;

    ReservaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaService(repositorio, verificarParticipante, estoque);
    }

    @Test
    void deveReservarQuandoParticipanteExisteEHaEstoque() {
        when(verificarParticipante.participanteExiste(4L)).thenReturn(true);
        when(repositorio.salvar(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = service.reservar(new NovaReserva(4L, List.of(new ItemReservaComando(1L, 2))));

        assertThat(reserva.participanteId()).isEqualTo(4L);
        verify(estoque).reservarUnidades(1L, 2);
        verify(repositorio).salvar(any(Reserva.class));
    }

    @Test
    void naoDeveReservarQuandoParticipanteNaoExiste() {
        when(verificarParticipante.participanteExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.reservar(new NovaReserva(99L, List.of(new ItemReservaComando(1L, 2)))))
                .isInstanceOf(ReservaInvalidaException.class);

        verify(estoque, never()).reservarUnidades(any(), anyInt());
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void naoDeveReservarSemItens() {
        when(verificarParticipante.participanteExiste(4L)).thenReturn(true);

        assertThatThrownBy(() -> service.reservar(new NovaReserva(4L, List.of())))
                .isInstanceOf(ReservaInvalidaException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradaAoBuscarIdInexistente() {
        when(repositorio.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(404L)).isInstanceOf(ReservaNaoEncontradaException.class);
    }

    @Test
    void deveCancelarELiberarEstoqueDeTodosOsItens() {
        Reserva reserva = Reserva.criar(4L, List.of(new ItemReserva(1L, 2), new ItemReserva(2L, 1)),
                Duration.ofMinutes(10));
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(comId(reserva, 1L)));
        when(repositorio.salvar(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva cancelada = service.cancelar(1L);

        assertThat(cancelada.status().name()).isEqualTo("CANCELADA");
        verify(estoque).liberarUnidades(1L, 2);
        verify(estoque).liberarUnidades(2L, 1);
    }

    @Test
    void deveExpirarReservasVencidasELiberarEstoque() {
        Reserva reserva = comId(Reserva.criar(4L, List.of(new ItemReserva(1L, 3)), Duration.ofMinutes(10)), 1L);
        when(repositorio.listarAtivasExpiradasAte(any(LocalDateTime.class))).thenReturn(List.of(reserva));
        when(repositorio.salvar(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        int total = service.expirarVencidas();

        assertThat(total).isEqualTo(1);
        verify(estoque).liberarUnidades(eq(1L), eq(3));
        verify(repositorio).salvar(any(Reserva.class));
    }

    /** Helper: dá um id a uma reserva recém-criada (sem id) para simular uma já persistida. */
    private static Reserva comId(Reserva reserva, Long id) {
        return Reserva.reconstituir(id, reserva.participanteId(), reserva.itens(), reserva.expiraEm(),
                reserva.status(), reserva.criadaEm());
    }
}
