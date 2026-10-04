package com.chezticket.catalogo.categoria.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.categoria.application.port.out.CategoriaRepositorio;
import com.chezticket.catalogo.categoria.domain.Categoria;
import com.chezticket.catalogo.categoria.domain.exception.CategoriaJaExisteException;
import com.chezticket.catalogo.categoria.domain.exception.CategoriaNaoEncontradaException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testa a orquestração do {@link CategoriaService} com a porta de saída mockada —
 * não toca banco de dados (isso é papel do {@code CategoriaPersistenceAdapterIT}).
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    CategoriaRepositorio repositorio;

    CategoriaService service;

    @BeforeEach
    void setUp() {
        service = new CategoriaService(repositorio);
    }

    private Categoria existente(Long id, String nome, boolean ativa) {
        return Categoria.reconstituir(id, nome, nome.toLowerCase(), ativa, LocalDateTime.now());
    }

    @Test
    void deveCadastrarQuandoNomeAindaNaoExiste() {
        when(repositorio.existePorNome("Shows")).thenReturn(false);
        when(repositorio.salvar(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria categoria = service.cadastrar("Shows");

        assertThat(categoria.nome()).isEqualTo("Shows");
        verify(repositorio).salvar(any(Categoria.class));
    }

    @Test
    void naoDeveCadastrarQuandoNomeJaExiste() {
        when(repositorio.existePorNome("Shows")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar("Shows"))
                .isInstanceOf(CategoriaJaExisteException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradaAoBuscarIdInexistente() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(CategoriaNaoEncontradaException.class);
    }

    @Test
    void listarDevolveAtivasOuTodasConformeOFiltro() {
        when(repositorio.listarAtivas()).thenReturn(List.of());
        when(repositorio.listarTodas()).thenReturn(List.of());

        service.listar(true);
        service.listar(false);

        verify(repositorio).listarAtivas();
        verify(repositorio).listarTodas();
    }

    @Test
    void deveAtualizarQuandoNovoNomeEstaLivre() {
        Categoria categoria = existente(1L, "Antigo", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(repositorio.existePorNomeExcetoId("Novo Nome", 1L)).thenReturn(false);
        when(repositorio.salvar(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria atualizada = service.atualizar(1L, "Novo Nome");

        assertThat(atualizada.nome()).isEqualTo("Novo Nome");
        assertThat(atualizada.slug()).isEqualTo("novo-nome");
    }

    @Test
    void naoDeveAtualizarParaNomeDeOutraCategoria() {
        Categoria categoria = existente(1L, "Antigo", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(repositorio.existePorNomeExcetoId("Esportes", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, "Esportes"))
                .isInstanceOf(CategoriaJaExisteException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void ativarEDesativarDevemPersistirONovoEstado() {
        Categoria categoria = existente(1L, "Shows", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(repositorio.salvar(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.desativar(1L).ativa()).isFalse();
        assertThat(service.ativar(1L).ativa()).isTrue();
    }

    @Test
    void deveRemoverQuandoCategoriaExiste() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente(1L, "Shows", true)));

        service.remover(1L);

        verify(repositorio).remover(1L);
    }

    @Test
    void naoDeveRemoverQuandoCategoriaNaoExiste() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(99L))
                .isInstanceOf(CategoriaNaoEncontradaException.class);

        verify(repositorio, never()).remover(anyLong());
    }
}
