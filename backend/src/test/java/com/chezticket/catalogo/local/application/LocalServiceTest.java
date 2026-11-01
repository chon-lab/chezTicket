package com.chezticket.catalogo.local.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.local.application.port.in.DadosLocal;
import com.chezticket.catalogo.local.application.port.in.NovoLocal;
import com.chezticket.catalogo.local.application.port.out.LocalRepositorio;
import com.chezticket.catalogo.local.domain.Local;
import com.chezticket.catalogo.local.domain.exception.LocalNaoEncontradoException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalServiceTest {

    @Mock
    LocalRepositorio repositorio;

    LocalService service;

    @BeforeEach
    void setUp() {
        service = new LocalService(repositorio);
    }

    @Test
    void deveCadastrarUmLocalValido() {
        when(repositorio.salvar(any(Local.class))).thenAnswer(inv -> inv.getArgument(0));

        Local local = service.cadastrar(new NovoLocal("Teatro Municipal", "Praça Central, 100",
                "Rio de Janeiro", "RJ", 1200));

        assertThat(local.nome()).isEqualTo("Teatro Municipal");
        verify(repositorio).salvar(any(Local.class));
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarIdInexistente() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L)).isInstanceOf(LocalNaoEncontradoException.class);
    }

    @Test
    void deveAtualizarUmLocalExistente() {
        Local existente = Local.reconstituir(1L, "Antigo", "Rua A", "Cidade", "RJ", 100,
                java.time.LocalDateTime.now());
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repositorio.salvar(any(Local.class))).thenAnswer(inv -> inv.getArgument(0));

        Local atualizado = service.atualizar(1L, new DadosLocal("Novo Nome", "Rua B", "Outra Cidade", "SP", 500));

        assertThat(atualizado.nome()).isEqualTo("Novo Nome");
        assertThat(atualizado.uf()).isEqualTo("SP");
    }

    @Test
    void naoDeveRemoverQuandoLocalNaoExiste() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(99L)).isInstanceOf(LocalNaoEncontradoException.class);
        verify(repositorio, never()).remover(any());
    }
}
