package com.chezticket.vendas.pagamento.adapter.out.persistence;

import com.chezticket.vendas.pagamento.application.port.out.ReembolsoRepositorio;
import com.chezticket.vendas.pagamento.domain.Reembolso;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class ReembolsoPersistenceAdapter implements ReembolsoRepositorio {

    private final ReembolsoJpaRepository jpa;

    ReembolsoPersistenceAdapter(ReembolsoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Reembolso salvar(Reembolso reembolso) {
        return ReembolsoPersistenceMapper.paraDominio(jpa.save(ReembolsoPersistenceMapper.paraEntidade(reembolso)));
    }

    @Override
    public Optional<Reembolso> buscarPorId(Long id) {
        return jpa.findById(id).map(ReembolsoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Reembolso> listarPorPagamento(Long pagamentoId) {
        return jpa.findAllByPagamentoId(pagamentoId).stream().map(ReembolsoPersistenceMapper::paraDominio).toList();
    }
}
