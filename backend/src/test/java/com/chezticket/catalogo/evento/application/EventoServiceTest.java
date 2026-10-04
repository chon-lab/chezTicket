package com.chezticket.catalogo.evento.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.evento.application.port.in.DadosEvento;
import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.evento.application.port.out.VerificarCategoriaPort;
import com.chezticket.catalogo.evento.application.port.out.VerificarPapelPort;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import com.chezticket.catalogo.evento.domain.StatusEvento;
import com.chezticket.catalogo.evento.domain.exception.EventoInvalidoException;
import com.chezticket.catalogo.evento.domain.exception.EventoNaoEncontradoException;
import com.chezticket.catalogo.evento.domain.exception.RemocaoNaoAutorizadaException;
import com.chezticket.catalogo.evento.domain.exception.TransicaoDeStatusInvalidaException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testa a orquestração do {@link EventoService} com as portas de saída mockadas —
 * não toca banco de dados (isso é papel do {@code EventoPersistenceAdapterIT}).
 */
@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    EventoRepositorio repositorio;

    @Mock
    VerificarCategoriaPort verificarCategoria;

    @Mock
    VerificarPapelPort verificarPapel;

    EventoService service;

    @BeforeEach
    void setUp() {
        service = new EventoService(repositorio, verificarCategoria, verificarPapel);
    }

    private Evento rascunhoExistente() {
        return Evento.reconstituir(1L, 2L, 1L, "Festival", "Descricao do evento", ClassificacaoEtaria.LIVRE,
                null, StatusEvento.RASCUNHO, PoliticaReembolso.PADRAO, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void deveCriarQuandoCategoriaExiste() {
        when(verificarCategoria.categoriaExiste(1L)).thenReturn(true);
        when(repositorio.salvar(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        Evento evento = service.criar(new NovoEvento(2L, 1L, "Festival de Inverno", "Desc", null, null, null));

        assertThat(evento.status()).isEqualTo(StatusEvento.RASCUNHO);
        verify(repositorio).salvar(any(Evento.class));
    }

    @Test
    void naoDeveCriarQuandoCategoriaNaoExiste() {
        when(verificarCategoria.categoriaExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.criar(
                new NovoEvento(2L, 99L, "Festival de Inverno", "Desc", null, null, null)))
                .isInstanceOf(EventoInvalidoException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarEventoInexistente() {
        when(repositorio.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(404L))
                .isInstanceOf(EventoNaoEncontradoException.class);
    }

    @Test
    void listarDelegaParaORepositorioComOFiltro() {
        FiltroEventos filtro = new FiltroEventos(StatusEvento.PUBLICADO, null, null);
        when(repositorio.buscar(filtro)).thenReturn(List.of());

        service.listar(filtro);

        verify(repositorio).buscar(filtro);
    }

    @Test
    void deveAtualizarQuandoCategoriaNovaExiste() {
        Evento evento = rascunhoExistente();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(verificarCategoria.categoriaExiste(6L)).thenReturn(true);
        when(repositorio.salvar(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        Evento atualizado = service.atualizar(1L,
                new DadosEvento(6L, "Novo Titulo do Evento", "Nova descricao", null, null, null));

        assertThat(atualizado.categoriaId()).isEqualTo(6L);
        assertThat(atualizado.titulo()).isEqualTo("Novo Titulo do Evento");
    }

    @Test
    void naoDeveAtualizarParaCategoriaInexistente() {
        Evento evento = rascunhoExistente();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(verificarCategoria.categoriaExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.atualizar(1L,
                new DadosEvento(99L, "Titulo Valido", "desc", null, null, null)))
                .isInstanceOf(EventoInvalidoException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void devePublicarEventoComDescricao() {
        Evento evento = rascunhoExistente();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(repositorio.salvar(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        Evento publicado = service.publicar(1L);

        assertThat(publicado.status()).isEqualTo(StatusEvento.PUBLICADO);
        assertThat(publicado.dataPublicacao()).isNotNull();
    }

    @Test
    void naoDevePublicarEventoJaPublicado() {
        Evento evento = rascunhoExistente();
        evento.publicar();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));

        assertThatThrownBy(() -> service.publicar(1L))
                .isInstanceOf(TransicaoDeStatusInvalidaException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void qualquerUmPodeRemoverRascunhoSemPrecisarDeAdmin() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(rascunhoExistente()));

        service.remover(1L, null);

        verify(repositorio).remover(1L);
        verify(verificarPapel, never()).ehAdministrador(any());
    }

    @Test
    void naoAdministradorNaoRemoveEventoPublicado() {
        Evento evento = rascunhoExistente();
        evento.publicar();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(verificarPapel.ehAdministrador(7L)).thenReturn(false);

        assertThatThrownBy(() -> service.remover(1L, 7L))
                .isInstanceOf(RemocaoNaoAutorizadaException.class);

        verify(repositorio, never()).remover(anyLong());
    }

    @Test
    void semSolicitanteInformadoNaoRemoveEventoPublicado() {
        Evento evento = rascunhoExistente();
        evento.publicar();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(verificarPapel.ehAdministrador(null)).thenReturn(false);

        assertThatThrownBy(() -> service.remover(1L, null))
                .isInstanceOf(RemocaoNaoAutorizadaException.class);

        verify(repositorio, never()).remover(anyLong());
    }

    @Test
    void administradorRemoveEventoPublicado() {
        Evento evento = rascunhoExistente();
        evento.publicar();
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(evento));
        when(verificarPapel.ehAdministrador(3L)).thenReturn(true);

        service.remover(1L, 3L);

        verify(repositorio).remover(1L);
    }
}
