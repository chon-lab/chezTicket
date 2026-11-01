package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class TipoIngressoPersistenceAdapter implements TipoIngressoRepositorio {

    private final TipoIngressoJpaRepository jpa;

    TipoIngressoPersistenceAdapter(TipoIngressoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public TipoIngresso salvar(TipoIngresso tipoIngresso) {
        return TipoIngressoPersistenceMapper.paraDominio(
                jpa.save(TipoIngressoPersistenceMapper.paraEntidade(tipoIngresso)));
    }

    @Override
    public Optional<TipoIngresso> buscarPorId(Long id) {
        return jpa.findById(id).map(TipoIngressoPersistenceMapper::paraDominio);
    }

    @Override
    public List<TipoIngresso> listarPorSessao(Long sessaoId) {
        return jpa.findAllBySessaoIdOrderByNomeAsc(sessaoId).stream()
                .map(TipoIngressoPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }
}
