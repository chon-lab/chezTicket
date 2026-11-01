package com.chezticket.vendas.reserva.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.vendas.reserva.domain.exception.ReservaInvalidaException;
import com.chezticket.vendas.reserva.domain.exception.TransicaoDeStatusDaReservaInvalidaException;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReservaTest {

    private final List<ItemReserva> umItem = List.of(new ItemReserva(1L, 2));

    @Test
    void deveNascerAtivaComExpiracaoFutura() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMinutes(10));

        assertThat(reserva.status()).isEqualTo(StatusReserva.ATIVA);
        assertThat(reserva.expiraEm()).isAfter(reserva.criadaEm());
        assertThat(reserva.itens()).containsExactlyElementsOf(umItem);
        assertThat(reserva.expirada()).isFalse();
    }

    @Test
    void naoDeveCriarSemParticipante() {
        assertThatThrownBy(() -> Reserva.criar(null, umItem, Duration.ofMinutes(10)))
                .isInstanceOf(ReservaInvalidaException.class);
    }

    @Test
    void naoDeveCriarSemItens() {
        assertThatThrownBy(() -> Reserva.criar(4L, List.of(), Duration.ofMinutes(10)))
                .isInstanceOf(ReservaInvalidaException.class);
    }

    @Test
    void itemReservaNaoAceitaQuantidadeZeroOuNegativa() {
        assertThatThrownBy(() -> new ItemReserva(1L, 0)).isInstanceOf(ReservaInvalidaException.class);
        assertThatThrownBy(() -> new ItemReserva(1L, -1)).isInstanceOf(ReservaInvalidaException.class);
        assertThatThrownBy(() -> new ItemReserva(null, 1)).isInstanceOf(ReservaInvalidaException.class);
    }

    @Test
    void deveDetectarReservaExpirada() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMillis(1));

        await10Millis();

        assertThat(reserva.expirada()).isTrue();
    }

    @Test
    void deveExpirarUmaReservaAtiva() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMinutes(10));

        reserva.expirar();

        assertThat(reserva.status()).isEqualTo(StatusReserva.EXPIRADA);
    }

    @Test
    void naoDeveExpirarDuasVezes() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMinutes(10));
        reserva.expirar();

        assertThatThrownBy(reserva::expirar).isInstanceOf(TransicaoDeStatusDaReservaInvalidaException.class);
    }

    @Test
    void deveCancelarUmaReservaAtiva() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMinutes(10));

        reserva.cancelar();

        assertThat(reserva.status()).isEqualTo(StatusReserva.CANCELADA);
    }

    @Test
    void naoDeveCancelarUmaReservaJaCancelada() {
        Reserva reserva = Reserva.criar(4L, umItem, Duration.ofMinutes(10));
        reserva.cancelar();

        assertThatThrownBy(reserva::cancelar).isInstanceOf(TransicaoDeStatusDaReservaInvalidaException.class);
    }

    private static void await10Millis() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
