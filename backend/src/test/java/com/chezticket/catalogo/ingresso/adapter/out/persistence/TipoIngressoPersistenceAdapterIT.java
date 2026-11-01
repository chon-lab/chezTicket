package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.support.MariaDbTestContainer;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testa a fiação real do módulo de tipo de ingresso: adaptador &rarr; Spring Data &rarr; MariaDB (container)
 * &rarr; Flyway, incluindo o bloqueio otimista do controle de estoque. Evento, sessão e local
 * pré-requisitos são inseridos direto via JDBC (localId=1 vem do seed V3).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, TipoIngressoPersistenceAdapter.class})
class TipoIngressoPersistenceAdapterIT {

    @Autowired
    TipoIngressoPersistenceAdapter adapter;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    EntityManager entityManager;

    private Long inserirSessao() {
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
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private TipoIngresso novoTipo(Long sessaoId) {
        return TipoIngresso.criar(sessaoId, "Inteira", false, new BigDecimal("100.00"), new BigDecimal("10.00"),
                50, 5);
    }

    @Test
    void deveSalvarERecuperarUmTipoDeIngresso() {
        Long sessaoId = inserirSessao();

        TipoIngresso salvo = adapter.salvar(novoTipo(sessaoId));

        assertThat(salvo.id()).isNotNull();
        assertThat(adapter.buscarPorId(salvo.id())).isPresent();
        assertThat(adapter.listarPorSessao(sessaoId)).extracting(TipoIngresso::nome).contains("Inteira");
    }

    @Test
    void deveRemoverUmTipoSemVendas() {
        Long sessaoId = inserirSessao();
        TipoIngresso salvo = adapter.salvar(novoTipo(sessaoId));

        adapter.remover(salvo.id());

        assertThat(adapter.buscarPorId(salvo.id())).isEmpty();
    }

    @Test
    void duasAtualizacoesConcorrentesDaMesmaVersaoDevemFalharNaSegunda() {
        // @DataJpaTest roda o teste numa única transação/sessão; sem limpar o contexto de
        // persistência entre as "duas leituras", o Hibernate reaproveitaria a mesma instância
        // já gerenciada por identidade e nunca enxergaria duas cópias de fato desatualizadas
        // uma em relação à outra — o que mascararia o próprio bloqueio otimista que este teste
        // existe para provar. flush()+clear() simulam duas sessões/transações independentes,
        // como aconteceria de verdade com duas requisições HTTP concorrentes.
        Long sessaoId = inserirSessao();
        Long id = adapter.salvar(novoTipo(sessaoId)).id();
        entityManager.flush();
        entityManager.clear();

        TipoIngresso copiaA = adapter.buscarPorId(id).orElseThrow();
        entityManager.clear();
        TipoIngresso copiaB = adapter.buscarPorId(id).orElseThrow();
        entityManager.clear();

        copiaA.reservarUnidades(2);
        adapter.salvar(copiaA);
        entityManager.flush();
        entityManager.clear();

        copiaB.reservarUnidades(3);
        assertThatThrownBy(() -> {
            adapter.salvar(copiaB);
            entityManager.flush();
        }).isInstanceOf(OptimisticLockingFailureException.class);

        // Confere via SQL puro, não pela mesma sessão JPA que acabou de falhar no flush().
        Integer quantidadeVendida = jdbc.queryForObject(
                "SELECT quantidade_vendida FROM tipo_ingresso WHERE id = ?", Integer.class, id);
        assertThat(quantidadeVendida).isEqualTo(2);
    }

    @Test
    void listarPorSessaoNaoRetornaTiposDeOutraSessao() {
        Long sessaoA = inserirSessao();
        Long sessaoB = inserirSessao();
        adapter.salvar(novoTipo(sessaoA));

        List<TipoIngresso> daSessaoB = adapter.listarPorSessao(sessaoB);

        assertThat(daSessaoB).isEmpty();
    }
}
