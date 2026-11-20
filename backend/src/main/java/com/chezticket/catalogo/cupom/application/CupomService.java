package com.chezticket.catalogo.cupom.application;

import com.chezticket.catalogo.cupom.application.port.in.CriarCupomUseCase;
import com.chezticket.catalogo.cupom.application.port.in.ValidarCupomUseCase;
import com.chezticket.catalogo.cupom.application.port.out.CupomRepositorio;
import com.chezticket.catalogo.cupom.domain.Cupom;
import com.chezticket.catalogo.cupom.domain.exception.CodigoCupomEmUsoException;
import com.chezticket.catalogo.cupom.domain.exception.CupomInvalidoException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CupomService implements CriarCupomUseCase, ValidarCupomUseCase {

    private static final Pattern CODIGO_VALIDO = Pattern.compile("[A-Za-z0-9]{8}");

    private final CupomRepositorio repositorio;

    public CupomService(CupomRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Cupom criar(Long eventoId, String codigo, int percentualDesconto, LocalDateTime validoAte) {
        String codigoNormalizado = normalizarCodigo(codigo);
        if (eventoId == null || !codigoValido(codigoNormalizado)
                || percentualDesconto < 1 || percentualDesconto > 100
                || validoAte == null || !validoAte.isAfter(LocalDateTime.now())) {
            throw new CupomInvalidoException();
        }
        if (repositorio.existePorCodigo(codigoNormalizado)) {
            throw new CodigoCupomEmUsoException();
        }
        return repositorio.salvar(new Cupom(null, eventoId, codigoNormalizado, percentualDesconto, validoAte));
    }

    @Override
    @Transactional(readOnly = true)
    public Cupom validar(Long eventoId, String codigo) {
        String codigoNormalizado = normalizarCodigo(codigo);
        if (eventoId == null || !codigoValido(codigoNormalizado)) {
            throw new CupomInvalidoException();
        }

        Cupom cupom = repositorio.buscarPorCodigo(codigoNormalizado).orElseThrow(CupomInvalidoException::new);
        if (cupom.validoAte() == null
                || !eventoId.equals(cupom.eventoId())
                || !LocalDateTime.now().isBefore(cupom.validoAte())) {
            throw new CupomInvalidoException();
        }
        return cupom;
    }

    private boolean codigoValido(String codigo) {
        return codigo != null && CODIGO_VALIDO.matcher(codigo).matches();
    }

    private String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.toUpperCase(Locale.ROOT);
    }
}
