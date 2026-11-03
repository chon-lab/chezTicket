package com.chezticket.catalogo.sessao.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.catalogo.sessao.domain.StatusSessao;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SessaoPersistenceMapperTest {

    @Test
    void persisteEReconstruiEnderecoDigitadoSemLocalId() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(5);
        String endereco = "Rua Exemplo, 100, Rio de Janeiro, RJ";
        Sessao sessao = Sessao.agendarComEndereco(7L, endereco, inicio, inicio.plusHours(2));

        SessaoJpaEntity entidade = SessaoPersistenceMapper.paraEntidade(sessao);
        Sessao reconstruida = SessaoPersistenceMapper.paraDominio(new SessaoJpaEntity(
                11L,
                entidade.getEventoId(),
                entidade.getLocalId(),
                entidade.getEndereco(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getStatus()));

        assertThat(entidade.getLocalId()).isNull();
        assertThat(entidade.getEndereco()).isEqualTo(endereco);
        assertThat(reconstruida.localId()).isNull();
        assertThat(reconstruida.endereco()).isEqualTo(endereco);
    }
}
