package com.chezticket.catalogo.sessao.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.catalogo.sessao.domain.StatusSessao;
import com.chezticket.support.MariaDbTestContainer;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de sessão: adaptador &rarr; Spring Data &rarr; MariaDB (container) &rarr; Flyway.
 * O evento pré-requisito é inserido direto via JDBC para não depender do adaptador (package-private)
 * de outro módulo; organizadorId=2 e categoriaId=1 vêm dos dados de demonstração (V3), assim como
 * localId=1.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, SessaoPersistenceAdapter.class})
class SessaoPersistenceAdapterIT {

    @Autowired
    SessaoPersistenceAdapter adapter;

    @Autowired
    JdbcTemplate jdbc;

    private Long inserirEvento() {
        jdbc.update("""
                INSERT INTO evento (organizador_id, categoria_id, titulo, status, classificacao_etaria,
                                     politica_reembolso)
                VALUES (2, 1, 'Evento de Teste IT', 'RASCUNHO', 'LIVRE', 'PADRAO')
                """);
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Sessao novaSessao(Long eventoId, LocalDateTime inicio) {
        return Sessao.agendar(eventoId, 1L, inicio, inicio.plusHours(3));
    }

    @Test
    void deveSalvarERecuperarUmaSessao() {
        Long eventoId = inserirEvento();

        Sessao salva = adapter.salvar(novaSessao(eventoId, LocalDateTime.now().plusDays(1)));

        assertThat(salva.id()).isNotNull();
        assertThat(adapter.buscarPorId(salva.id())).isPresent();
        assertThat(adapter.existePorId(salva.id())).isTrue();
    }

    @Test
    void deveFiltrarPorEventoEStatus() {
        Long eventoId = inserirEvento();
        Sessao cancelada = novaSessao(eventoId, LocalDateTime.now().plusDays(2));
        cancelada.cancelar();
        adapter.salvar(cancelada);
        adapter.salvar(novaSessao(eventoId, LocalDateTime.now().plusDays(3)));

        List<Sessao> doEvento = adapter.buscar(new FiltroSessoes(eventoId, null));
        assertThat(doEvento).hasSize(2);

        List<Sessao> canceladas = adapter.buscar(new FiltroSessoes(null, StatusSessao.CANCELADA));
        assertThat(canceladas).extracting(Sessao::status).containsOnly(StatusSessao.CANCELADA);
    }

    @Test
    void deveRemoverUmaSessaoAgendada() {
        Long eventoId = inserirEvento();
        Sessao salva = adapter.salvar(novaSessao(eventoId, LocalDateTime.now().plusDays(4)));

        adapter.remover(salva.id());

        assertThat(adapter.buscarPorId(salva.id())).isEmpty();
    }
}
