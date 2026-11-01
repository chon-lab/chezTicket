package com.chezticket.catalogo.sessao.application;

import com.chezticket.catalogo.sessao.application.port.in.AgendarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.AtualizarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.ConsultarSessoesUseCase;
import com.chezticket.catalogo.sessao.application.port.in.DadosSessao;
import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.application.port.in.GerenciarCicloDeVidaDaSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import com.chezticket.catalogo.sessao.application.port.in.RemoverSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.out.SessaoRepositorio;
import com.chezticket.catalogo.sessao.application.port.out.VerificarEventoPort;
import com.chezticket.catalogo.sessao.application.port.out.VerificarLocalPort;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.catalogo.sessao.domain.exception.SessaoInvalidaException;
import com.chezticket.catalogo.sessao.domain.exception.SessaoNaoEncontradaException;
import com.chezticket.catalogo.sessao.domain.exception.SessaoNaoPodeSerRemovidaException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessaoService implements
        AgendarSessaoUseCase,
        AtualizarSessaoUseCase,
        ConsultarSessoesUseCase,
        GerenciarCicloDeVidaDaSessaoUseCase,
        RemoverSessaoUseCase {

    private final SessaoRepositorio repositorio;
    private final VerificarEventoPort verificarEvento;
    private final VerificarLocalPort verificarLocal;

    public SessaoService(SessaoRepositorio repositorio, VerificarEventoPort verificarEvento,
            VerificarLocalPort verificarLocal) {
        this.repositorio = repositorio;
        this.verificarEvento = verificarEvento;
        this.verificarLocal = verificarLocal;
    }

    @Override
    public Sessao agendar(NovaSessao comando) {
        exigirEventoExistente(comando.eventoId());
        exigirLocalExistente(comando.localId());
        Sessao sessao = Sessao.agendar(comando.eventoId(), comando.localId(), comando.inicio(), comando.fim());
        return repositorio.salvar(sessao);
    }

    @Override
    public Sessao atualizar(Long sessaoId, DadosSessao dados) {
        Sessao sessao = carregar(sessaoId);
        exigirLocalExistente(dados.localId());
        sessao.atualizarDados(dados.localId(), dados.inicio(), dados.fim());
        return repositorio.salvar(sessao);
    }

    @Override
    @Transactional(readOnly = true)
    public Sessao buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> listar(FiltroSessoes filtro) {
        return repositorio.buscar(filtro != null ? filtro : FiltroSessoes.vazio());
    }

    @Override
    public Sessao iniciar(Long sessaoId) {
        Sessao sessao = carregar(sessaoId);
        sessao.iniciar();
        return repositorio.salvar(sessao);
    }

    @Override
    public Sessao encerrar(Long sessaoId) {
        Sessao sessao = carregar(sessaoId);
        sessao.encerrar();
        return repositorio.salvar(sessao);
    }

    @Override
    public Sessao cancelar(Long sessaoId) {
        Sessao sessao = carregar(sessaoId);
        sessao.cancelar();
        return repositorio.salvar(sessao);
    }

    @Override
    public void remover(Long sessaoId) {
        Sessao sessao = carregar(sessaoId);
        if (!sessao.podeSerRemovida()) {
            throw new SessaoNaoPodeSerRemovidaException(sessaoId, sessao.status());
        }
        repositorio.remover(sessaoId);
    }

    private Sessao carregar(Long sessaoId) {
        return repositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(sessaoId));
    }

    private void exigirEventoExistente(Long eventoId) {
        if (!verificarEvento.eventoExiste(eventoId)) {
            throw new SessaoInvalidaException("Evento inexistente: " + eventoId + ".");
        }
    }

    private void exigirLocalExistente(Long localId) {
        if (!verificarLocal.localExiste(localId)) {
            throw new SessaoInvalidaException("Local inexistente: " + localId + ".");
        }
    }
}
