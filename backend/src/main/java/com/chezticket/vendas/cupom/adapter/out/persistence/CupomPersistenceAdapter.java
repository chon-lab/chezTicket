package com.chezticket.vendas.cupom.adapter.out.persistence;

import com.chezticket.vendas.cupom.application.port.out.CupomRepositorio;
import com.chezticket.vendas.cupom.domain.Cupom;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class CupomPersistenceAdapter implements CupomRepositorio {

    private final CupomJpaRepository jpa;

    CupomPersistenceAdapter(CupomJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cupom salvar(Cupom cupom) {
        return CupomPersistenceMapper.paraDominio(jpa.save(CupomPersistenceMapper.paraEntidade(cupom)));
    }

    @Override
    public Optional<Cupom> buscarPorEventoECodigo(Long eventoId, String codigo) {
        return jpa.findByEventoIdAndCodigo(eventoId, codigo).map(CupomPersistenceMapper::paraDominio);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return jpa.existsByCodigo(codigo);
    }
}
