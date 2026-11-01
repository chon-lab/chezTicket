package com.chezticket.catalogo.local.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.local.domain.exception.LocalInvalidoException;
import org.junit.jupiter.api.Test;

class LocalTest {

    @Test
    void deveCriarComDadosValidos() {
        Local local = Local.novo("Teatro Municipal", "Praça Central, 100", "Rio de Janeiro", "rj", 1200);

        assertThat(local.nome()).isEqualTo("Teatro Municipal");
        assertThat(local.uf()).isEqualTo("RJ");
        assertThat(local.capacidadeTotal()).isEqualTo(1200);
        assertThat(local.criadoEm()).isNotNull();
    }

    @Test
    void naoDeveCriarComNomeCurto() {
        assertThatThrownBy(() -> Local.novo("A", "Rua X", "Cidade", "RJ", 100))
                .isInstanceOf(LocalInvalidoException.class);
    }

    @Test
    void naoDeveCriarComEnderecoOuCidadeEmBranco() {
        assertThatThrownBy(() -> Local.novo("Teatro Municipal", " ", "Cidade", "RJ", 100))
                .isInstanceOf(LocalInvalidoException.class);
        assertThatThrownBy(() -> Local.novo("Teatro Municipal", "Rua X", " ", "RJ", 100))
                .isInstanceOf(LocalInvalidoException.class);
    }

    @Test
    void naoDeveCriarComUfInvalida() {
        assertThatThrownBy(() -> Local.novo("Teatro Municipal", "Rua X", "Cidade", "Rio", 100))
                .isInstanceOf(LocalInvalidoException.class);
    }

    @Test
    void naoDeveCriarComCapacidadeZeroOuNegativa() {
        assertThatThrownBy(() -> Local.novo("Teatro Municipal", "Rua X", "Cidade", "RJ", 0))
                .isInstanceOf(LocalInvalidoException.class);
        assertThatThrownBy(() -> Local.novo("Teatro Municipal", "Rua X", "Cidade", "RJ", -10))
                .isInstanceOf(LocalInvalidoException.class);
    }

    @Test
    void deveAtualizarDados() {
        Local local = Local.novo("Teatro Municipal", "Praça Central, 100", "Rio de Janeiro", "RJ", 1200);

        local.atualizarDados("Teatro Municipal Renovado", "Praça Central, 101", "Niterói", "rj", 1500);

        assertThat(local.nome()).isEqualTo("Teatro Municipal Renovado");
        assertThat(local.cidade()).isEqualTo("Niterói");
        assertThat(local.capacidadeTotal()).isEqualTo(1500);
    }
}
