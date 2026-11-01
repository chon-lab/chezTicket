package com.chezticket.catalogo.local.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.local.domain.Local;
import com.chezticket.support.MariaDbTestContainer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Testa a fiação real do módulo de local: adaptador &rarr; Spring Data &rarr; MariaDB (container) &rarr; Flyway.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, LocalPersistenceAdapter.class})
class LocalPersistenceAdapterIT {

    @Autowired
    LocalPersistenceAdapter adapter;

    @Test
    void deveSalvarERecuperarUmLocal() {
        Local salvo = adapter.salvar(Local.novo("Arena de Testes", "Rua dos Testes, 1", "Rio de Janeiro", "RJ",
                500));

        assertThat(salvo.id()).isNotNull();
        assertThat(adapter.buscarPorId(salvo.id())).isPresent();
        assertThat(adapter.existePorId(salvo.id())).isTrue();
    }

    @Test
    void deveEnxergarOsLocaisDoSeedDoFlyway() {
        List<Local> locais = adapter.listarTodos();

        assertThat(locais).extracting(Local::nome)
                .contains("Auditório Principal — CEFET/RJ Maracanã", "Teatro Municipal Exemplo");
    }

    @Test
    void deveRemoverUmLocalSemSessoesVinculadas() {
        Local salvo = adapter.salvar(Local.novo("Local Para Remover", "Rua X, 1", "Cidade", "RJ", 100));

        adapter.remover(salvo.id());

        assertThat(adapter.buscarPorId(salvo.id())).isEmpty();
    }
}
