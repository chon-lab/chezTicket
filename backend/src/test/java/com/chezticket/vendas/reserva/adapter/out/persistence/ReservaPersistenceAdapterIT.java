package com.chezticket.vendas.reserva.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.support.MariaDbTestContainer;
import com.chezticket.vendas.reserva.domain.ItemReserva;
import com.chezticket.vendas.reserva.domain.Reserva;
import com.chezticket.vendas.reserva.domain.StatusReserva;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de reserva: adaptador &rarr; Spring Data &rarr; MariaDB (container)
 * &rarr; Flyway, incluindo a persistência dos itens (tabela {@code item_reserva}, V4). O tipo de
 * ingresso pré-requisito é inserido direto via JDBC; participanteId=4 vem do seed de demonstração (V4).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, ReservaPersistenceAdapter.class})
class ReservaPersistenceAdapterIT {

    private static final Long PARTICIPANTE_DEMO_ID = 4L;

    @Autowired
    ReservaPersistenceAdapter adapter;

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

    private Reserva novaReserva(Long tipoIngressoId) {
        return Reserva.criar(PARTICIPANTE_DEMO_ID, List.of(new ItemReserva(tipoIngressoId, 2)),
                Duration.ofMinutes(10));
    }

    @Test
    void deveSalvarERecuperarUmaReservaComSeusItens() {
        Long tipoIngressoId = inserirTipoIngresso();

        Reserva salva = adapter.salvar(novaReserva(tipoIngressoId));

        assertThat(salva.id()).isNotNull();
        Reserva recuperada = adapter.buscarPorId(salva.id()).orElseThrow();
        assertThat(recuperada.itens()).containsExactly(new ItemReserva(tipoIngressoId, 2));
        assertThat(recuperada.status()).isEqualTo(StatusReserva.ATIVA);
    }

    @Test
    void deveListarPorParticipante() {
        Long tipoIngressoId = inserirTipoIngresso();
        adapter.salvar(novaReserva(tipoIngressoId));

        List<Reserva> doParticipante = adapter.listarPorParticipante(PARTICIPANTE_DEMO_ID);

        assertThat(doParticipante).isNotEmpty();
        assertThat(doParticipante).extracting(Reserva::participanteId).containsOnly(PARTICIPANTE_DEMO_ID);
    }

    @Test
    void deveEncontrarReservasAtivasExpiradasAteUmaReferencia() {
        Long tipoIngressoId = inserirTipoIngresso();
        Reserva jaVencida = Reserva.criar(PARTICIPANTE_DEMO_ID, List.of(new ItemReserva(tipoIngressoId, 1)),
                Duration.ofMillis(1));
        adapter.salvar(jaVencida);
        adapter.salvar(novaReserva(tipoIngressoId));

        List<Reserva> vencidas = adapter.listarAtivasExpiradasAte(LocalDateTime.now());

        assertThat(vencidas).hasSize(1);
        assertThat(vencidas.get(0).status()).isEqualTo(StatusReserva.ATIVA);
    }

    @Test
    void statusAtualizadoDevePersistirSemDuplicarItens() {
        Long tipoIngressoId = inserirTipoIngresso();
        Reserva salva = adapter.salvar(novaReserva(tipoIngressoId));

        Reserva paraCancelar = adapter.buscarPorId(salva.id()).orElseThrow();
        paraCancelar.cancelar();
        adapter.salvar(paraCancelar);

        Reserva recuperada = adapter.buscarPorId(salva.id()).orElseThrow();
        assertThat(recuperada.status()).isEqualTo(StatusReserva.CANCELADA);
        assertThat(recuperada.itens()).hasSize(1);
    }
}
