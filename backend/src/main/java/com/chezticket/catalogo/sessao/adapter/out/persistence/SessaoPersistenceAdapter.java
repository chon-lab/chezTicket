package com.chezticket.catalogo.sessao.adapter.out.persistence;

import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.application.port.out.SessaoRepositorio;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class SessaoPersistenceAdapter implements SessaoRepositorio {

    private final SessaoJpaRepository jpa;

    SessaoPersistenceAdapter(SessaoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Sessao salvar(Sessao sessao) {
        return SessaoPersistenceMapper.paraDominio(
                jpa.save(SessaoPersistenceMapper.paraEntidade(sessao)));
    }

    @Override
    public Optional<Sessao> buscarPorId(Long id) {
        return jpa.findById(id).map(SessaoPersistenceMapper::paraDominio);
    }

    @Override
    public boolean existePorId(Long id) {
        return id != null && jpa.existsById(id);
    }

    @Override
    public List<Sessao> buscar(FiltroSessoes filtro) {
        return jpa.buscar(filtro.eventoId(), filtro.status()).stream()
                .map(SessaoPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }
}
