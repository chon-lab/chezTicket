package com.chezticket.catalogo.sessao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.sessao.application.port.in.DadosSessao;
import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import com.chezticket.catalogo.sessao.application.port.out.SessaoRepositorio;
import com.chezticket.catalogo.sessao.application.port.out.VerificarEventoPort;
import com.chezticket.catalogo.sessao.application.port.out.VerificarLocalPort;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.catalogo.sessao.domain.StatusSessao;
import com.chezticket.catalogo.sessao.domain.exception.SessaoInvalidaException;
import com.chezticket.catalogo.sessao.domain.exception.SessaoNaoEncontradaException;
import com.chezticket.catalogo.sessao.domain.exception.SessaoNaoPodeSerRemovidaException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    SessaoRepositorio repositorio;

    @Mock
    VerificarEventoPort verificarEvento;

    @Mock
    VerificarLocalPort verificarLocal;

    SessaoService service;

    private final LocalDateTime inicio = LocalDateTime.now().plusDays(5);
    private final LocalDateTime fim = inicio.plusHours(2);

    @BeforeEach
    void setUp() {
        service = new SessaoService(repositorio, verificarEvento, verificarLocal);
    }

    private Sessao agendadaExistente() {
        return Sessao.reconstituir(1L, 1L, 1L, inicio, fim, StatusSessao.AGENDADA);
    }

    @Test
    void deveAgendarQuandoEventoELocalExistem() {
        when(verificarEvento.eventoExiste(1L)).thenReturn(true);
        when(verificarLocal.localExiste(1L)).thenReturn(true);
        when(repositorio.salvar(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        Sessao sessao = service.agendar(new NovaSessao(1L, 1L, inicio, fim));

        assertThat(sessao.status()).isEqualTo(StatusSessao.AGENDADA);
    }

    @Test
    void naoDeveAgendarQuandoEventoNaoExiste() {
        when(verificarEvento.eventoExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.agendar(new NovaSessao(99L, 1L, inicio, fim)))
                .isInstanceOf(SessaoInvalidaException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void naoDeveAgendarQuandoLocalNaoExiste() {
        when(verificarEvento.eventoExiste(1L)).thenReturn(true);
        when(verificarLocal.localExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.agendar(new NovaSessao(1L, 99L, inicio, fim)))
                .isInstanceOf(SessaoInvalidaException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradaAoBuscarIdInexistente() {
        when(repositorio.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(404L)).isInstanceOf(SessaoNaoEncontradaException.class);
    }

    @Test
    void deveAtualizarQuandoNovoLocalExiste() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(agendadaExistente()));
        when(verificarLocal.localExiste(2L)).thenReturn(true);
        when(repositorio.salvar(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        Sessao atualizada = service.atualizar(1L, new DadosSessao(2L, inicio, fim));

        assertThat(atualizada.localId()).isEqualTo(2L);
    }

    @Test
    void qualquerUmPodeRemoverSessaoAgendada() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(agendadaExistente()));

        service.remover(1L);

        verify(repositorio).remover(1L);
    }

    @Test
    void naoDeveRemoverSessaoJaIniciada() {
        Sessao sessao = agendadaExistente();
        sessao.iniciar();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.remover(1L)).isInstanceOf(SessaoNaoPodeSerRemovidaException.class);

        verify(repositorio, never()).remover(any());
    }

    @Test
    void deveIniciarUmaSessaoAgendada() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(agendadaExistente()));
        when(repositorio.salvar(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        Sessao iniciada = service.iniciar(1L);

        assertThat(iniciada.status()).isEqualTo(StatusSessao.EM_ANDAMENTO);
    }
}
