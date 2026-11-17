package com.chezticket.catalogo.cupom.adapter.out.persistence;

import com.chezticket.catalogo.cupom.application.port.out.CupomRepositorio;
import com.chezticket.catalogo.cupom.domain.Cupom;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class CupomPersistenceAdapter implements CupomRepositorio {

    private final CupomJpaRepository jpa;

    CupomPersistenceAdapter(CupomJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return jpa.existsByCodigo(codigo);
    }

    @Override
    public Optional<Cupom> buscarPorCodigo(String codigo) {
        return jpa.findByCodigoAndTipo(codigo, "PERCENTUAL").map(this::paraDominio);
    }

    @Override
    public Cupom salvar(Cupom cupom) {
        CupomJpaEntity entidade = new CupomJpaEntity(
                cupom.id(),
                cupom.eventoId(),
                cupom.codigo(),
                BigDecimal.valueOf(cupom.percentualDesconto()),
                cupom.validoAte());
        return paraDominio(jpa.save(entidade));
    }

    private Cupom paraDominio(CupomJpaEntity entidade) {
        if (!"PERCENTUAL".equals(entidade.getTipo()) || entidade.getValor() == null) {
            throw new IllegalStateException("O cupom armazenado possui uma configuração não suportada.");
        }
        return new Cupom(
                entidade.getId(),
                entidade.getEventoId(),
                entidade.getCodigo(),
                entidade.getValor().intValueExact(),
                entidade.getValidoAte());
    }
}
