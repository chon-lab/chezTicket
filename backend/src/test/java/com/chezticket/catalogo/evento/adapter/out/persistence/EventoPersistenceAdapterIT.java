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
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Autowired
    JdbcTemplate jdbc;

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

    @Test
    void deveRemoverEventoERegistrosDeVendaAssociados() {
        Evento evento = novoRascunho("Evento Com Venda Para Remover");
        evento.publicar();
        Evento salvo = adapter.salvar(evento);
        Long participanteId = jdbc.queryForObject(
                "SELECT MIN(usuario_id) FROM participante", Long.class);
        jdbc.update(
                "INSERT INTO local (nome, endereco, cidade, uf, capacidade_total) "
                        + "VALUES ('Local de teste', 'Rua de teste', 'São Paulo', 'SP', 10)");
        Long localId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        jdbc.update(
                "INSERT INTO sessao (evento_id, local_id, inicio, fim) "
                        + "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR))",
                salvo.id(), localId);
        Long sessaoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update(
                "INSERT INTO tipo_ingresso (sessao_id, nome, quantidade_total, quantidade_vendida) "
                        + "VALUES (?, 'Inteira', 1, 1)",
                sessaoId);
        Long tipoIngressoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update(
                "INSERT INTO reserva (participante_id, expira_em, status) "
                        + "VALUES (?, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONVERTIDA')",
                participanteId);
        Long reservaId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update(
                "INSERT INTO item_reserva (reserva_id, tipo_ingresso_id, quantidade) VALUES (?, ?, 1)",
                reservaId, tipoIngressoId);
        jdbc.update(
                "INSERT INTO pedido (participante_id, reserva_id, status, subtotal, valor_taxa, valor_total) "
                        + "VALUES (?, ?, 'PAGO', 100.00, 5.00, 105.00)",
                participanteId, reservaId);
        Long pedidoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update(
                "INSERT INTO item_pedido (pedido_id, tipo_ingresso_id, quantidade, preco_unitario) "
                        + "VALUES (?, ?, 1, 100.00)",
                pedidoId, tipoIngressoId);
        jdbc.update(
                "INSERT INTO pagamento (pedido_id, metodo, valor, status) VALUES (?, 'PIX', 105.00, 'APROVADO')",
                pedidoId);
        Long pagamentoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update(
                "INSERT INTO reembolso (pagamento_id, motivo, tipo, valor, status) "
                        + "VALUES (?, 'CANCELAMENTO_ORGANIZADOR', 'TOTAL', 105.00, 'PROCESSADO')",
                pagamentoId);
        jdbc.update(
                "INSERT INTO ingresso (pedido_id, tipo_ingresso_id, codigo_qr) VALUES (?, ?, 'qr-teste-evento')",
                pedidoId, tipoIngressoId);
        jdbc.update(
                "INSERT INTO repasse_financeiro (evento_id, valor_bruto, valor_taxa, valor_liquido) "
                        + "VALUES (?, 105.00, 5.00, 100.00)",
                salvo.id());

        adapter.remover(salvo.id());

        assertThat(adapter.buscarPorId(salvo.id())).isEmpty();
        assertThat(contar("sessao", "evento_id", salvo.id())).isZero();
        assertThat(contar("tipo_ingresso", "sessao_id", sessaoId)).isZero();
        assertThat(contar("reserva", "id", reservaId)).isZero();
        assertThat(contar("pedido", "id", pedidoId)).isZero();
        assertThat(contar("pagamento", "pedido_id", pedidoId)).isZero();
        assertThat(contar("reembolso", "pagamento_id", pagamentoId)).isZero();
        assertThat(contar("item_pedido", "pedido_id", pedidoId)).isZero();
        assertThat(contar("ingresso", "pedido_id", pedidoId)).isZero();
        assertThat(contar("repasse_financeiro", "evento_id", salvo.id())).isZero();
    }

    private int contar(String tabela, String coluna, Long id) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + tabela + " WHERE " + coluna + " = ?",
                Integer.class,
                id);
    }
}
