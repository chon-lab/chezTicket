package com.chezticket.vendas.ingresso.adapter.out.persistence;

import com.chezticket.vendas.ingresso.application.port.out.IngressoRepositorio;
import com.chezticket.vendas.ingresso.domain.Ingresso;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class IngressoPersistenceAdapter implements IngressoRepositorio {

    private final IngressoJpaRepository jpa;

    IngressoPersistenceAdapter(IngressoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Ingresso salvar(Ingresso ingresso) {
        return IngressoPersistenceMapper.paraDominio(jpa.save(IngressoPersistenceMapper.paraEntidade(ingresso)));
    }

    @Override
    public Optional<Ingresso> buscarPorId(Long id) {
        return jpa.findById(id).map(IngressoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Ingresso> listarPorPedido(Long pedidoId) {
        return jpa.findAllByPedidoId(pedidoId).stream().map(IngressoPersistenceMapper::paraDominio).toList();
    }
}
