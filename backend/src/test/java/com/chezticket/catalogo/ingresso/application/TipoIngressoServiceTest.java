package com.chezticket.catalogo.ingresso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.ingresso.application.port.in.DadosTipoIngresso;
import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.application.port.out.VerificarSessaoPort;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoInvalidoException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoEncontradoException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoPodeSerRemovidoException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TipoIngressoServiceTest {

    @Mock
    TipoIngressoRepositorio repositorio;

    @Mock
    VerificarSessaoPort verificarSessao;

    TipoIngressoService service;

    @BeforeEach
    void setUp() {
        service = new TipoIngressoService(repositorio, verificarSessao);
    }

    private TipoIngresso existente() {
        return TipoIngresso.reconstituir(1L, 1L, "Inteira", false, new BigDecimal("100.00"),
                new BigDecimal("10.00"), 50, 0, 5, 0);
    }

    @Test
    void deveCadastrarQuandoSessaoExiste() {
        when(verificarSessao.sessaoExiste(1L)).thenReturn(true);
        when(repositorio.salvar(any(TipoIngresso.class))).thenAnswer(inv -> inv.getArgument(0));

        TipoIngresso tipo = service.cadastrar(new NovoTipoIngresso(1L, "Inteira", false,
                new BigDecimal("100.00"), new BigDecimal("10.00"), 50, 5));

        assertThat(tipo.nome()).isEqualTo("Inteira");
        verify(repositorio).salvar(any(TipoIngresso.class));
    }

    @Test
    void naoDeveCadastrarQuandoSessaoNaoExiste() {
        when(verificarSessao.sessaoExiste(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.cadastrar(new NovoTipoIngresso(99L, "Inteira", true, null, null, 50, 5)))
                .isInstanceOf(TipoIngressoInvalidoException.class);

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarIdInexistente() {
        when(repositorio.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(404L))
                .isInstanceOf(TipoIngressoNaoEncontradoException.class);
    }

    @Test
    void deveAtualizarUmTipoExistente() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente()));
        when(repositorio.salvar(any(TipoIngresso.class))).thenAnswer(inv -> inv.getArgument(0));

        TipoIngresso atualizado = service.atualizar(1L, new DadosTipoIngresso("Inteira Promo", false,
                new BigDecimal("80.00"), new BigDecimal("8.00"), 60, 4));

        assertThat(atualizado.nome()).isEqualTo("Inteira Promo");
        assertThat(atualizado.quantidadeTotal()).isEqualTo(60);
    }

    @Test
    void deveRemoverQuandoNaoHaUnidadesVendidas() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente()));

        service.remover(1L);

        verify(repositorio).remover(1L);
    }

    @Test
    void naoDeveRemoverQuandoJaHaUnidadesVendidas() {
        TipoIngresso tipo = existente();
        tipo.reservarUnidades(1);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(tipo));

        assertThatThrownBy(() -> service.remover(1L))
                .isInstanceOf(TipoIngressoNaoPodeSerRemovidoException.class);

        verify(repositorio, never()).remover(any());
    }
}
