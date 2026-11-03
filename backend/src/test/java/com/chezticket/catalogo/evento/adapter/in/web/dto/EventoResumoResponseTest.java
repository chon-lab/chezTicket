package com.chezticket.catalogo.evento.adapter.in.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.evento.domain.Evento;
import org.junit.jupiter.api.Test;

class EventoResumoResponseTest {

    @Test
    void incluiImagemDeCapaNaRespostaResumo() {
        Evento evento = Evento.criarRascunho(
                2L,
                1L,
                "Evento de tecnologia",
                "Descricao do evento",
                null,
                "/api/uploads/capa.jpg",
                null);

        EventoResumoResponse resposta = EventoResumoResponse.de(evento);

        assertThat(resposta.imagemCapa()).isEqualTo("/api/uploads/capa.jpg");
    }
}
