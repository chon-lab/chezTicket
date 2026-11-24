package com.chezticket.vendas.pedido.adapter.out.persistence;

import com.chezticket.vendas.pedido.application.port.out.PedidoRepositorio;
import com.chezticket.vendas.pedido.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class PedidoPersistenceAdapter implements PedidoRepositorio {

    private final PedidoJpaRepository jpa;

    PedidoPersistenceAdapter(PedidoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        return PedidoPersistenceMapper.paraDominio(jpa.save(PedidoPersistenceMapper.paraEntidade(pedido)));
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return jpa.findById(id).map(PedidoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Pedido> listarPorParticipante(Long participanteId) {
        return jpa.findAllByParticipanteIdOrderByCriadoEmDesc(participanteId).stream()
                .map(PedidoPersistenceMapper::paraDominio)
                .toList();
    }
}
