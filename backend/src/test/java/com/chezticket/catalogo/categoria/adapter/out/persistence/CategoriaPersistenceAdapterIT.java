package com.chezticket.catalogo.categoria.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.categoria.domain.Categoria;
import com.chezticket.support.MariaDbTestContainer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Testa a fiação real: adaptador &rarr; Spring Data &rarr; MariaDB (container) &rarr; Flyway.
 * Prova que a conexão com o banco e as migrações estão de pé.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MariaDbTestContainer.class, CategoriaPersistenceAdapter.class})
class CategoriaPersistenceAdapterIT {

    @Autowired
    CategoriaPersistenceAdapter adapter;

    @Test
    void deveSalvarERecuperarUmaCategoria() {
        Categoria salva = adapter.salvar(Categoria.nova("Feira de Tecnologia"));

        assertThat(salva.id()).isNotNull();
        assertThat(adapter.buscarPorId(salva.id())).isPresent();
        assertThat(adapter.listarTodas()).extracting(Categoria::nome).contains("Feira de Tecnologia");
    }

    @Test
    void deveDetectarNomeJaExistente() {
        adapter.salvar(Categoria.nova("Stand-up Comedy"));

        assertThat(adapter.existePorNome("Stand-up Comedy")).isTrue();
        assertThat(adapter.existePorNome("Ópera")).isFalse();
    }

    @Test
    void deveEnxergarAsCategoriasDoSeedDoFlyway() {
        List<Categoria> ativas = adapter.listarAtivas();

        assertThat(ativas).extracting(Categoria::slug)
                .contains("esportes", "institucional-cefet-rj");
    }
}
