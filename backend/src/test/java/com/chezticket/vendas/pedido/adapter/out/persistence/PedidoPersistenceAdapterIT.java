package com.chezticket.vendas.pedido.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.support.MariaDbTestContainer;
import com.chezticket.vendas.pedido.domain.ItemPedido;
import com.chezticket.vendas.pedido.domain.Pedido;
import com.chezticket.vendas.pedido.domain.StatusPedido;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de pedido: adaptador &rarr; Spring Data &rarr; MariaDB (container)
 * &rarr; Flyway, incluindo a persistência dos itens (tabela {@code item_pedido}, V1). Os
 * pré-requisitos (tipo de ingresso, reserva) são inseridos direto via JDBC; participanteId=4
 * vem do seed de demonstração (V4).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, PedidoPersistenceAdapter.class})
class PedidoPersistenceAdapterIT {

    private static final Long PARTICIPANTE_DEMO_ID = 4L;

    @Autowired
    PedidoPersistenceAdapter adapter;

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

    private Long inserirReserva() {
        jdbc.update("""
                INSERT INTO reserva (participante_id, expira_em, status, criada_em)
                VALUES (?, DATE_ADD(NOW(), INTERVAL 10 MINUTE), 'ATIVA', NOW())
                """, PARTICIPANTE_DEMO_ID);
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Pedido novoPedido(Long reservaId, Long tipoIngressoId) {
        return Pedido.criar(PARTICIPANTE_DEMO_ID, reservaId, null,
                List.of(new ItemPedido(tipoIngressoId, 2, new BigDecimal("100.00"))), new BigDecimal("10.00"), 0);
    }

    @Test
    void deveSalvarERecuperarUmPedidoComSeusItens() {
        Long tipoIngressoId = inserirTipoIngresso();
        Long reservaId = inserirReserva();

        Pedido salvo = adapter.salvar(novoPedido(reservaId, tipoIngressoId));

        assertThat(salvo.id()).isNotNull();
        Pedido recuperado = adapter.buscarPorId(salvo.id()).orElseThrow();
        assertThat(recuperado.itens()).containsExactly(new ItemPedido(tipoIngressoId, 2, new BigDecimal("100.00")));
        assertThat(recuperado.status()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(recuperado.subtotal()).isEqualByComparingTo("200.00");
        assertThat(recuperado.valorTotal()).isEqualByComparingTo("210.00");
    }

    @Test
    void deveListarPorParticipante() {
        Long tipoIngressoId = inserirTipoIngresso();
        Long reservaId = inserirReserva();
        adapter.salvar(novoPedido(reservaId, tipoIngressoId));

        List<Pedido> doParticipante = adapter.listarPorParticipante(PARTICIPANTE_DEMO_ID);

        assertThat(doParticipante).isNotEmpty();
        assertThat(doParticipante).extracting(Pedido::participanteId).containsOnly(PARTICIPANTE_DEMO_ID);
    }

    @Test
    void statusAtualizadoDevePersistirSemDuplicarItens() {
        Long tipoIngressoId = inserirTipoIngresso();
        Long reservaId = inserirReserva();
        Pedido salvo = adapter.salvar(novoPedido(reservaId, tipoIngressoId));

        Pedido paraConfirmar = adapter.buscarPorId(salvo.id()).orElseThrow();
        paraConfirmar.confirmarPagamento();
        paraConfirmar.concluir();
        adapter.salvar(paraConfirmar);

        Pedido recuperado = adapter.buscarPorId(salvo.id()).orElseThrow();
        assertThat(recuperado.status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(recuperado.itens()).hasSize(1);
    }
}
