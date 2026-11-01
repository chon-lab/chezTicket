package com.chezticket.catalogo.ingresso.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.ingresso.domain.exception.EstoqueInsuficienteException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoInvalidoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TipoIngressoTest {

    private TipoIngresso pagoValido() {
        return TipoIngresso.criar(1L, "Inteira", false, new BigDecimal("100.00"), new BigDecimal("10.00"), 50, 5);
    }

    @Test
    void deveCriarUmTipoPago() {
        TipoIngresso tipo = pagoValido();

        assertThat(tipo.gratuito()).isFalse();
        assertThat(tipo.preco()).isEqualByComparingTo("100.00");
        assertThat(tipo.quantidadeVendida()).isZero();
        assertThat(tipo.quantidadeDisponivel()).isEqualTo(50);
    }

    @Test
    void deveZerarPrecoETaxaQuandoGratuito() {
        TipoIngresso tipo = TipoIngresso.criar(1L, "Gratuito", true, new BigDecimal("999.00"), null, 100, 5);

        assertThat(tipo.preco()).isEqualByComparingTo("0.00");
        assertThat(tipo.taxaServico()).isEqualByComparingTo("0.00");
    }

    @Test
    void naoDeveCriarPagoSemPrecoPositivo() {
        assertThatThrownBy(() -> TipoIngresso.criar(1L, "Inteira", false, BigDecimal.ZERO, BigDecimal.ZERO, 50, 5))
                .isInstanceOf(TipoIngressoInvalidoException.class);
        assertThatThrownBy(() -> TipoIngresso.criar(1L, "Inteira", false, null, BigDecimal.ZERO, 50, 5))
                .isInstanceOf(TipoIngressoInvalidoException.class);
    }

    @Test
    void naoDeveCriarComQuantidadeTotalZeroOuNegativa() {
        assertThatThrownBy(() -> TipoIngresso.criar(1L, "Inteira", true, null, null, 0, 5))
                .isInstanceOf(TipoIngressoInvalidoException.class);
    }

    @Test
    void naoDeveCriarComLimitePorCompraMaiorQueQuantidadeTotal() {
        assertThatThrownBy(() -> TipoIngresso.criar(1L, "Inteira", true, null, null, 10, 20))
                .isInstanceOf(TipoIngressoInvalidoException.class);
    }

    @Test
    void temDisponibilidadeRespeitaOEstoque() {
        TipoIngresso tipo = pagoValido();

        assertThat(tipo.temDisponibilidade(50)).isTrue();
        assertThat(tipo.temDisponibilidade(51)).isFalse();
        assertThat(tipo.temDisponibilidade(0)).isFalse();
    }

    @Test
    void deveReservarUnidadesDentroDoEstoqueELimite() {
        TipoIngresso tipo = pagoValido();

        tipo.reservarUnidades(5);

        assertThat(tipo.quantidadeVendida()).isEqualTo(5);
        assertThat(tipo.quantidadeDisponivel()).isEqualTo(45);
    }

    @Test
    void naoDeveReservarAcimaDoLimitePorCompra() {
        TipoIngresso tipo = pagoValido();

        assertThatThrownBy(() -> tipo.reservarUnidades(6)).isInstanceOf(TipoIngressoInvalidoException.class);
    }

    @Test
    void naoDeveReservarQuandoEstoqueInsuficiente() {
        TipoIngresso tipo = TipoIngresso.criar(1L, "Ultimas Unidades", true, null, null, 10, 5);
        tipo.reservarUnidades(5);
        tipo.reservarUnidades(3);

        assertThatThrownBy(() -> tipo.reservarUnidades(3)).isInstanceOf(EstoqueInsuficienteException.class);
    }

    @Test
    void deveLiberarUnidadesDeVolta() {
        TipoIngresso tipo = pagoValido();
        tipo.reservarUnidades(5);

        tipo.liberarUnidades(3);

        assertThat(tipo.quantidadeVendida()).isEqualTo(2);
    }

    @Test
    void liberarNuncaDeixaQuantidadeVendidaNegativa() {
        TipoIngresso tipo = pagoValido();

        tipo.liberarUnidades(100);

        assertThat(tipo.quantidadeVendida()).isZero();
    }

    @Test
    void naoDeveAtualizarParaQuantidadeTotalMenorQueVendida() {
        TipoIngresso tipo = pagoValido();
        tipo.reservarUnidades(5);
        tipo.reservarUnidades(5);

        assertThatThrownBy(() -> tipo.atualizarDados("Inteira", false, new BigDecimal("100.00"),
                new BigDecimal("10.00"), 5, 5))
                .isInstanceOf(TipoIngressoInvalidoException.class);
    }

    @Test
    void podeSerRemovidoApenasSemVendas() {
        TipoIngresso tipo = pagoValido();
        assertThat(tipo.podeSerRemovido()).isTrue();

        tipo.reservarUnidades(1);
        assertThat(tipo.podeSerRemovido()).isFalse();
    }
}
