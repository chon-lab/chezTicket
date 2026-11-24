package com.chezticket.vendas.ingresso.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.support.MariaDbTestContainer;
import com.chezticket.vendas.ingresso.domain.Ingresso;
import com.chezticket.vendas.ingresso.domain.StatusIngresso;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de ingresso: adaptador &rarr; Spring Data &rarr; MariaDB
 * (container) &rarr; Flyway. Os pré-requisitos (pedido, tipo de ingresso) são inseridos direto
 * via JDBC; participanteId=4 vem do seed de demonstração (V4).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, IngressoPersistenceAdapter.class})
class IngressoPersistenceAdapterIT {

    @Autowired
    IngressoPersistenceAdapter adapter;

    @Autowired
    JdbcTemplate jdbc;

    private Long inserirTipoIngresso() {
        jdbc.update("""
                INSERT INTO evento (organizador_id, categoria_id, titulo, status, classificacao_etaria,
                                     politica_reembolso)
                VALUES (2, 1, 'Evento de Teste IT', 'RASCUNHO', 'LIVRE', 'PADRAO')
                """);
        Long eventoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update("""
                INSERT INTO sessao (evento_id, local_id, inicio, fim, status)
                VALUES (?, 1, DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY) + INTERVAL 3 HOUR,
                        'AGENDADA')
                """, eventoId);
        Long sessaoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update("""
                INSERT INTO tipo_ingresso (sessao_id, nome, gratuito, preco, taxa_servico, quantidade_total,
                                            limite_por_compra)
                VALUES (?, 'Inteira', 0, 100.00, 10.00, 50, 5)
                """, sessaoId);
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long inserirPedido() {
        jdbc.update("INSERT INTO pedido (participante_id) VALUES (4)");
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Test
    void deveSalvarERecuperarUmIngresso() {
        Long tipoIngressoId = inserirTipoIngresso();
        Long pedidoId = inserirPedido();

        Ingresso salvo = adapter.salvar(Ingresso.emitir(pedidoId, tipoIngressoId, "Fulano de Tal"));

        assertThat(salvo.id()).isNotNull();
        assertThat(salvo.codigoQr()).isNotBlank();
        Ingresso recuperado = adapter.buscarPorId(salvo.id()).orElseThrow();
        assertThat(recuperado.status()).isEqualTo(StatusIngresso.EMITIDO);
        assertThat(recuperado.titular()).isEqualTo("Fulano de Tal");
    }

    @Test
    void deveListarPorPedido() {
        Long tipoIngressoId = inserirTipoIngresso();
        Long pedidoId = inserirPedido();
        adapter.salvar(Ingresso.emitir(pedidoId, tipoIngressoId, "A"));
        adapter.salvar(Ingresso.emitir(pedidoId, tipoIngressoId, "B"));

        List<Ingresso> doPedido = adapter.listarPorPedido(pedidoId);

        assertThat(doPedido).hasSize(2);
        assertThat(doPedido).extracting(Ingresso::codigoQr).doesNotHaveDuplicates();
    }
}
