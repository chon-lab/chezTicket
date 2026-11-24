package com.chezticket.vendas.cupom.application;

import com.chezticket.vendas.cupom.application.port.in.CriarCupomUseCase;
import com.chezticket.vendas.cupom.application.port.in.NovoCupom;
import com.chezticket.vendas.cupom.application.port.in.ValidarCupomUseCase;
import com.chezticket.vendas.cupom.application.port.out.CupomRepositorio;
import com.chezticket.vendas.cupom.domain.Cupom;
import com.chezticket.vendas.cupom.domain.exception.CupomExpiradoException;
import com.chezticket.vendas.cupom.domain.exception.CupomJaExisteException;
import com.chezticket.vendas.cupom.domain.exception.CupomNaoEncontradoException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CupomService implements CriarCupomUseCase, ValidarCupomUseCase {

    private final CupomRepositorio repositorio;

    public CupomService(CupomRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Cupom criar(NovoCupom comando) {
        Cupom cupom = Cupom.criar(comando.eventoId(), comando.codigo(), comando.percentualDesconto(),
                comando.validoAte());
        if (repositorio.existePorCodigo(cupom.codigo())) {
            throw new CupomJaExisteException(cupom.codigo());
        }
        return repositorio.salvar(cupom);
    }

    @Override
    @Transactional(readOnly = true)
    public Cupom validar(Long eventoId, String codigo) {
        String normalizado = codigo == null ? "" : codigo.strip().toUpperCase();
        Cupom cupom = repositorio.buscarPorEventoECodigo(eventoId, normalizado)
                .orElseThrow(() -> new CupomNaoEncontradoException(normalizado));
        if (!cupom.estaValido(LocalDate.now())) {
            throw new CupomExpiradoException(normalizado);
        }
        return cupom;
    }
}
