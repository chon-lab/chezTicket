package com.chezticket.catalogo.sessao.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.sessao.domain.exception.SessaoInvalidaException;
import com.chezticket.catalogo.sessao.domain.exception.TransicaoDeStatusDaSessaoInvalidaException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SessaoTest {

    private final LocalDateTime inicio = LocalDateTime.now().plusDays(10);
    private final LocalDateTime fim = inicio.plusHours(3);

    private Sessao agendadaValida() {
        return Sessao.agendar(1L, 1L, inicio, fim);
    }

    @Test
    void deveNascerComoAgendada() {
        Sessao sessao = agendadaValida();

        assertThat(sessao.status()).isEqualTo(StatusSessao.AGENDADA);
        assertThat(sessao.podeSerRemovida()).isTrue();
    }

    @Test
    void naoDeveCriarSemEventoOuLocal() {
        assertThatThrownBy(() -> Sessao.agendar(null, 1L, inicio, fim))
                .isInstanceOf(SessaoInvalidaException.class);
        assertThatThrownBy(() -> Sessao.agendar(1L, null, inicio, fim))
                .isInstanceOf(SessaoInvalidaException.class);
    }

    @Test
    void naoDeveCriarComFimAntesOuIgualAoInicio() {
        assertThatThrownBy(() -> Sessao.agendar(1L, 1L, inicio, inicio))
                .isInstanceOf(SessaoInvalidaException.class);
        assertThatThrownBy(() -> Sessao.agendar(1L, 1L, inicio, inicio.minusHours(1)))
                .isInstanceOf(SessaoInvalidaException.class);
    }

    @Test
    void deveIniciarEDepoisEncerrar() {
        Sessao sessao = agendadaValida();

        sessao.iniciar();
        assertThat(sessao.status()).isEqualTo(StatusSessao.EM_ANDAMENTO);
        assertThat(sessao.podeSerRemovida()).isFalse();

        sessao.encerrar();
        assertThat(sessao.status()).isEqualTo(StatusSessao.ENCERRADA);
    }

    @Test
    void naoDeveEncerrarSemEstarEmAndamento() {
        Sessao sessao = agendadaValida();

        assertThatThrownBy(sessao::encerrar).isInstanceOf(TransicaoDeStatusDaSessaoInvalidaException.class);
    }

    @Test
    void deveCancelarAPartirDeAgendadaOuEmAndamento() {
        Sessao sessao = agendadaValida();
        sessao.cancelar();
        assertThat(sessao.status()).isEqualTo(StatusSessao.CANCELADA);

        Sessao outra = agendadaValida();
        outra.iniciar();
        outra.cancelar();
        assertThat(outra.status()).isEqualTo(StatusSessao.CANCELADA);
    }

    @Test
    void naoDeveCancelarUmaSessaoJaEncerrada() {
        Sessao sessao = agendadaValida();
        sessao.iniciar();
        sessao.encerrar();

        assertThatThrownBy(sessao::cancelar).isInstanceOf(TransicaoDeStatusDaSessaoInvalidaException.class);
    }

    @Test
    void naoDeveReagendarUmaSessaoQueJaComecou() {
        Sessao sessao = agendadaValida();
        sessao.iniciar();

        assertThatThrownBy(() -> sessao.atualizarDados(2L, inicio, fim))
                .isInstanceOf(SessaoInvalidaException.class);
    }

    @Test
    void deveReagendarUmaSessaoAgendada() {
        Sessao sessao = agendadaValida();
        LocalDateTime novoInicio = inicio.plusDays(1);
        LocalDateTime novoFim = novoInicio.plusHours(2);

        sessao.atualizarDados(2L, novoInicio, novoFim);

        assertThat(sessao.localId()).isEqualTo(2L);
        assertThat(sessao.inicio()).isEqualTo(novoInicio);
        assertThat(sessao.fim()).isEqualTo(novoFim);
    }
}
