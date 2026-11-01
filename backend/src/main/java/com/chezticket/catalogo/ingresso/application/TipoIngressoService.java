package com.chezticket.catalogo.ingresso.application;

import com.chezticket.catalogo.ingresso.application.port.in.AtualizarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.CadastrarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.ConsultarTiposIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.DadosTipoIngresso;
import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import com.chezticket.catalogo.ingresso.application.port.in.RemoverTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.application.port.out.VerificarSessaoPort;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoInvalidoException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoEncontradoException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoNaoPodeSerRemovidoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TipoIngressoService implements
        CadastrarTipoIngressoUseCase,
        AtualizarTipoIngressoUseCase,
        ConsultarTiposIngressoUseCase,
        RemoverTipoIngressoUseCase {

    private final TipoIngressoRepositorio repositorio;
    private final VerificarSessaoPort verificarSessao;

    public TipoIngressoService(TipoIngressoRepositorio repositorio, VerificarSessaoPort verificarSessao) {
        this.repositorio = repositorio;
        this.verificarSessao = verificarSessao;
    }

    @Override
    public TipoIngresso cadastrar(NovoTipoIngresso comando) {
        if (!verificarSessao.sessaoExiste(comando.sessaoId())) {
            throw new TipoIngressoInvalidoException("Sessão inexistente: " + comando.sessaoId() + ".");
        }
        TipoIngresso tipoIngresso = TipoIngresso.criar(
                comando.sessaoId(),
                comando.nome(),
                comando.gratuito(),
                comando.preco(),
                comando.taxaServico(),
                comando.quantidadeTotal(),
                comando.limitePorCompra());
        return repositorio.salvar(tipoIngresso);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoIngresso buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoIngresso> listarPorSessao(Long sessaoId) {
        return repositorio.listarPorSessao(sessaoId);
    }

    @Override
    public TipoIngresso atualizar(Long id, DadosTipoIngresso dados) {
        TipoIngresso tipoIngresso = carregar(id);
        tipoIngresso.atualizarDados(dados.nome(), dados.gratuito(), dados.preco(), dados.taxaServico(),
                dados.quantidadeTotal(), dados.limitePorCompra());
        return repositorio.salvar(tipoIngresso);
    }

    @Override
    public void remover(Long id) {
        TipoIngresso tipoIngresso = carregar(id);
        if (!tipoIngresso.podeSerRemovido()) {
            throw new TipoIngressoNaoPodeSerRemovidoException(id);
        }
        repositorio.remover(id);
    }

    private TipoIngresso carregar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new TipoIngressoNaoEncontradoException(id));
    }
}
