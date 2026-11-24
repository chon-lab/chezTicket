package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.application.port.out.PagamentoRepositorio;
import com.chezticket.vendas.pagamento.domain.Pagamento;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class PagamentoPersistenceAdapter implements PagamentoRepositorio {

    private final PagamentoJpaRepository jpa;

    PagamentoPersistenceAdapter(PagamentoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return PagamentoPersistenceMapper.paraDominio(jpa.save(PagamentoPersistenceMapper.paraEntidade(pagamento)));
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        return jpa.findById(id).map(PagamentoPersistenceMapper::paraDominio);
    }

    @Override
    public Optional<Pagamento> buscarPorPedidoId(Long pedidoId) {
        return jpa.findByPedidoId(pedidoId).map(PagamentoPersistenceMapper::paraDominio);
    }
}
