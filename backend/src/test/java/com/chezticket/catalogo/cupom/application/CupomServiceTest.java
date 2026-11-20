package com.chezticket.catalogo.cupom.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.cupom.application.port.out.CupomRepositorio;
import com.chezticket.catalogo.cupom.domain.Cupom;
import com.chezticket.catalogo.cupom.domain.exception.CodigoCupomEmUsoException;
import com.chezticket.catalogo.cupom.domain.exception.CupomInvalidoException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CupomServiceTest {

    private final CupomRepositorio repositorio = mock(CupomRepositorio.class);
    private final CupomService service = new CupomService(repositorio);

    @Test
    void criaCupomPercentualComValidadeDoEvento() {
        LocalDateTime validoAte = LocalDateTime.now().plusDays(2);
        Cupom cupom = new Cupom(null, 10L, "12345678", 20, validoAte);
        when(repositorio.salvar(cupom)).thenReturn(new Cupom(1L, 10L, "12345678", 20, validoAte));

        assertThat(service.criar(10L, "12345678", 20, validoAte).percentualDesconto()).isEqualTo(20);
        verify(repositorio).salvar(cupom);
    }

    @Test
    void naoCriaCodigoDuplicado() {
        when(repositorio.existePorCodigo("12345678")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(10L, "12345678", 20, LocalDateTime.now().plusDays(2)))
                .isInstanceOf(CodigoCupomEmUsoException.class);
    }

    @Test
    void validaCupomApenasParaEventoEAntesDaValidade() {
        Cupom cupom = new Cupom(1L, 10L, "AB12CD34", 20, LocalDateTime.now().plusDays(2));
        when(repositorio.buscarPorCodigo("AB12CD34")).thenReturn(Optional.of(cupom));

        assertThat(service.validar(10L, "ab12cd34")).isEqualTo(cupom);
        assertThatThrownBy(() -> service.validar(11L, "AB12CD34"))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    void normalizaCodigoAlfanumericoAoCriar() {
        LocalDateTime validoAte = LocalDateTime.now().plusDays(2);
        Cupom cupomNormalizado = new Cupom(null, 10L, "AB12CD34", 20, validoAte);
        when(repositorio.salvar(cupomNormalizado)).thenReturn(
                new Cupom(2L, 10L, "AB12CD34", 20, validoAte));

        assertThat(service.criar(10L, "ab12cd34", 20, validoAte).codigo()).isEqualTo("AB12CD34");
        verify(repositorio).salvar(cupomNormalizado);
    }
}
