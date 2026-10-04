package com.chezticket.catalogo.evento.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.evento.domain.exception.EventoInvalidoException;
import com.chezticket.catalogo.evento.domain.exception.TransicaoDeStatusInvalidaException;
import org.junit.jupiter.api.Test;

class EventoTest {

    private Evento rascunhoValido() {
        return Evento.criarRascunho(2L, 1L, "Festival de Inverno",
                "Shows e oficinas no campus.", ClassificacaoEtaria.LIVRE, null, PoliticaReembolso.PADRAO);
    }

    @Test
    void deveNascerComoRascunhoComPadroesAplicados() {
        Evento evento = Evento.criarRascunho(2L, 1L, "  Mostra   de Curtas ", null, null, null, null);

        assertThat(evento.status()).isEqualTo(StatusEvento.RASCUNHO);
        assertThat(evento.titulo()).isEqualTo("Mostra de Curtas");
        assertThat(evento.classificacaoEtaria()).isEqualTo(ClassificacaoEtaria.LIVRE);
        assertThat(evento.politicaReembolso()).isEqualTo(PoliticaReembolso.PADRAO);
        assertThat(evento.dataPublicacao()).isNull();
        assertThat(evento.podeSerRemovido()).isTrue();
    }

    @Test
    void naoDeveCriarSemOrganizadorOuCategoria() {
        assertThatThrownBy(() -> Evento.criarRascunho(null, 1L, "X Y Z", null, null, null, null))
                .isInstanceOf(EventoInvalidoException.class);
        assertThatThrownBy(() -> Evento.criarRascunho(2L, null, "X Y Z", null, null, null, null))
                .isInstanceOf(EventoInvalidoException.class);
    }

    @Test
    void naoDeveCriarComTituloCurto() {
        assertThatThrownBy(() -> Evento.criarRascunho(2L, 1L, "ab", null, null, null, null))
                .isInstanceOf(EventoInvalidoException.class);
    }

    @Test
    void devePublicarUmRascunhoComDescricao() {
        Evento evento = rascunhoValido();

        evento.publicar();

        assertThat(evento.status()).isEqualTo(StatusEvento.PUBLICADO);
        assertThat(evento.dataPublicacao()).isNotNull();
        assertThat(evento.podeSerRemovido()).isFalse();
    }

    @Test
    void naoDevePublicarSemDescricao() {
        Evento evento = Evento.criarRascunho(2L, 1L, "Evento Sem Texto", null, null, null, null);

        assertThatThrownBy(evento::publicar).isInstanceOf(EventoInvalidoException.class);
    }

    @Test
    void naoDevePublicarDuasVezes() {
        Evento evento = rascunhoValido();
        evento.publicar();

        assertThatThrownBy(evento::publicar).isInstanceOf(TransicaoDeStatusInvalidaException.class);
    }

    @Test
    void deveCancelarEDepoisBloquearNovaTransicao() {
        Evento evento = rascunhoValido();
        evento.publicar();

        evento.cancelar();
        assertThat(evento.status()).isEqualTo(StatusEvento.CANCELADO);

        assertThatThrownBy(evento::cancelar).isInstanceOf(TransicaoDeStatusInvalidaException.class);
        assertThatThrownBy(evento::encerrar).isInstanceOf(TransicaoDeStatusInvalidaException.class);
    }

    @Test
    void naoDeveEditarEventoCancelado() {
        Evento evento = rascunhoValido();
        evento.cancelar();

        assertThatThrownBy(() -> evento.atualizarDados(1L, "Novo Titulo", "nova", null, null, null))
                .isInstanceOf(EventoInvalidoException.class);
    }

    @Test
    void deveEncerrarEventoPublicado() {
        Evento evento = rascunhoValido();
        evento.publicar();

        evento.encerrar();

        assertThat(evento.status()).isEqualTo(StatusEvento.ENCERRADO);
    }
}
