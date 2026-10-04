package com.chezticket.catalogo.categoria.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.categoria.domain.exception.CategoriaInvalidaException;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void deveCriarCategoriaAtivaComSlugDerivadoDoNome() {
        Categoria categoria = Categoria.nova("  Teatro   Infantil ");

        assertThat(categoria.nome()).isEqualTo("Teatro Infantil");
        assertThat(categoria.slug()).isEqualTo("teatro-infantil");
        assertThat(categoria.ativa()).isTrue();
        assertThat(categoria.id()).isNull();
        assertThat(categoria.criadaEm()).isNotNull();
    }

    @Test
    void deveRemoverAcentosAoGerarOSlug() {
        assertThat(Categoria.nova("Congressos e Palestras Acadêmicas").slug())
                .isEqualTo("congressos-e-palestras-academicas");
    }

    @Test
    void naoDeveCriarCategoriaComNomeEmBranco() {
        assertThatThrownBy(() -> Categoria.nova("   "))
                .isInstanceOf(CategoriaInvalidaException.class);
    }

    @Test
    void naoDeveCriarCategoriaComNomeMuitoCurto() {
        assertThatThrownBy(() -> Categoria.nova("A"))
                .isInstanceOf(CategoriaInvalidaException.class);
    }

    @Test
    void devePermitirAtivarEDesativar() {
        Categoria categoria = Categoria.nova("Shows e Música");

        categoria.desativar();
        assertThat(categoria.ativa()).isFalse();

        categoria.ativar();
        assertThat(categoria.ativa()).isTrue();
    }

    @Test
    void deveAtualizarNomeESlugAoRenomear() {
        Categoria categoria = Categoria.nova("Shows");

        categoria.renomear("Shows e Festivais");

        assertThat(categoria.nome()).isEqualTo("Shows e Festivais");
        assertThat(categoria.slug()).isEqualTo("shows-e-festivais");
    }
}
