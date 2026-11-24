package com.chezticket.vendas.pagamento.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.support.MariaDbTestContainer;
import com.chezticket.vendas.pagamento.domain.MetodoPagamento;
import com.chezticket.vendas.pagamento.domain.Pagamento;
import com.chezticket.vendas.pagamento.domain.StatusPagamento;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de pagamento: adaptador &rarr; Spring Data &rarr; MariaDB
 * (container) &rarr; Flyway. O pedido pré-requisito é inserido direto via JDBC; participanteId=4
 * vem do seed de demonstração (V4).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, PagamentoPersistenceAdapter.class})
class PagamentoPersistenceAdapterIT {

    @Autowired
    PagamentoPersistenceAdapter adapter;

    @Autowired
    JdbcTemplate jdbc;

    private Long inserirPedido() {
        jdbc.update("INSERT INTO pedido (participante_id) VALUES (4)");
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Test
    void deveSalvarERecuperarUmPagamento() {
        Long pedidoId = inserirPedido();

        Pagamento salvo = adapter.salvar(
                Pagamento.aprovar(pedidoId, "pi_123", MetodoPagamento.CARTAO_CREDITO, new BigDecimal("110.00")));

        assertThat(salvo.id()).isNotNull();
        Pagamento recuperado = adapter.buscarPorId(salvo.id()).orElseThrow();
        assertThat(recuperado.status()).isEqualTo(StatusPagamento.APROVADO);
        assertThat(recuperado.gateway()).isEqualTo("STRIPE");
        assertThat(recuperado.valor()).isEqualByComparingTo("110.00");
    }

    @Test
    void deveBuscarPorPedidoId() {
        Long pedidoId = inserirPedido();
        adapter.salvar(Pagamento.aprovar(pedidoId, "pi_456", MetodoPagamento.CARTAO_CREDITO,
                new BigDecimal("50.00")));

        Pagamento encontrado = adapter.buscarPorPedidoId(pedidoId).orElseThrow();

        assertThat(encontrado.idTransacao()).isEqualTo("pi_456");
    }
}
