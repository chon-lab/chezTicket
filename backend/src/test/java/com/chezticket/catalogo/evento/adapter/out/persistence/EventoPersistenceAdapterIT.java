package com.chezticket.catalogo.evento.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.evento.domain.StatusEvento;
import com.chezticket.support.MariaDbTestContainer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Testa a fiação real do módulo de eventos: adaptador &rarr; Spring Data &rarr; MariaDB
 * (container) &rarr; Flyway, incluindo a query de filtro dinâmico (status/categoria/organizador).
 * organizadorId=2 e categoriaId=1 vêm dos dados de demonstração (V3).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, EventoPersistenceAdapter.class})
class EventoPersistenceAdapterIT {

    @Autowired
    EventoPersistenceAdapter adapter;

    private Evento novoRascunho(String titulo) {
        return Evento.criarRascunho(2L, 1L, titulo, "Descricao de teste de integracao", null, null, null);
    }

    @Test
    void deveSalvarERecuperarUmEvento() {
        Evento salvo = adapter.salvar(novoRascunho("Evento de Integracao"));

        assertThat(salvo.id()).isNotNull();
        assertThat(adapter.buscarPorId(salvo.id())).isPresent();
    }

    @Test
    void deveFiltrarPorStatusCategoriaEOrganizador() {
        Evento publicado = novoRascunho("Evento Publicado IT");
        publicado.publicar();
        adapter.salvar(publicado);
        adapter.salvar(novoRascunho("Evento Rascunho IT"));

        List<Evento> publicados = adapter.buscar(new FiltroEventos(StatusEvento.PUBLICADO, null, null));
        assertThat(publicados).extracting(Evento::titulo).contains("Evento Publicado IT");
        assertThat(publicados).extracting(Evento::status).containsOnly(StatusEvento.PUBLICADO);

        List<Evento> daCategoria = adapter.buscar(new FiltroEventos(null, 1L, null));
        assertThat(daCategoria).extracting(Evento::titulo)
                .contains("Evento Publicado IT", "Evento Rascunho IT");

        List<Evento> doOrganizador = adapter.buscar(new FiltroEventos(null, null, 2L));
        assertThat(doOrganizador).isNotEmpty();
    }

    @Test
    void deveRemoverUmEvento() {
        Evento salvo = adapter.salvar(novoRascunho("Evento Para Remover"));

        adapter.remover(salvo.id());

        assertThat(adapter.buscarPorId(salvo.id())).isEmpty();
    }
}
