package com.chezticket.catalogo.evento.application;

import com.chezticket.catalogo.evento.application.port.in.AtualizarEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.ConsultarEventosUseCase;
import com.chezticket.catalogo.evento.application.port.in.CriarEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.DadosEvento;
import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.application.port.in.GerenciarCicloDeVidaDoEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.application.port.in.RemoverEventoUseCase;
import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.evento.application.port.out.VerificarCategoriaPort;
import com.chezticket.catalogo.evento.application.port.out.VerificarPapelPort;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.evento.domain.exception.EventoInvalidoException;
import com.chezticket.catalogo.evento.domain.exception.EventoNaoEncontradoException;
import com.chezticket.catalogo.evento.domain.exception.RemocaoNaoAutorizadaException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoService implements
        CriarEventoUseCase,
        AtualizarEventoUseCase,
        ConsultarEventosUseCase,
        GerenciarCicloDeVidaDoEventoUseCase,
        RemoverEventoUseCase {

    private final EventoRepositorio repositorio;
    private final VerificarCategoriaPort verificarCategoria;
    private final VerificarPapelPort verificarPapel;

    public EventoService(EventoRepositorio repositorio, VerificarCategoriaPort verificarCategoria,
            VerificarPapelPort verificarPapel) {
        this.repositorio = repositorio;
        this.verificarCategoria = verificarCategoria;
        this.verificarPapel = verificarPapel;
    }

    @Override
    public Evento criar(NovoEvento comando) {
        exigirCategoriaExistente(comando.categoriaId());
        Evento evento = Evento.criarRascunho(
                comando.organizadorId(),
                comando.categoriaId(),
                comando.titulo(),
                comando.descricao(),
                comando.classificacaoEtaria(),
                comando.imagemCapa(),
                comando.politicaReembolso());
        return repositorio.salvar(evento);
    }

    @Override
    public Evento atualizar(Long eventoId, DadosEvento dados) {
        Evento evento = carregar(eventoId);
        exigirCategoriaExistente(dados.categoriaId());
        evento.atualizarDados(
                dados.categoriaId(),
                dados.titulo(),
                dados.descricao(),
                dados.classificacaoEtaria(),
                dados.imagemCapa(),
                dados.politicaReembolso());
        return repositorio.salvar(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public Evento buscarPorId(Long eventoId) {
        return carregar(eventoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evento> listar(FiltroEventos filtro) {
        return repositorio.buscar(filtro != null ? filtro : FiltroEventos.vazio());
    }

    @Override
    public Evento publicar(Long eventoId) {
        Evento evento = carregar(eventoId);
        evento.publicar();
        return repositorio.salvar(evento);
    }

    @Override
    public Evento cancelar(Long eventoId) {
        Evento evento = carregar(eventoId);
        evento.cancelar();
        return repositorio.salvar(evento);
    }

    @Override
    public Evento encerrar(Long eventoId) {
        Evento evento = carregar(eventoId);
        evento.encerrar();
        return repositorio.salvar(evento);
    }

    @Override
    public void remover(Long eventoId, Long solicitanteId) {
        Evento evento = carregar(eventoId);
        if (!evento.podeSerRemovido() && !verificarPapel.ehAdministrador(solicitanteId)) {
            throw new RemocaoNaoAutorizadaException(eventoId, evento.status());
        }
        repositorio.remover(eventoId);
    }

    private Evento carregar(Long eventoId) {
        return repositorio.buscarPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventoId));
    }

    private void exigirCategoriaExistente(Long categoriaId) {
        if (categoriaId != null && !verificarCategoria.categoriaExiste(categoriaId)) {
            throw new EventoInvalidoException("Categoria inexistente: " + categoriaId + ".");
        }
    }
}
