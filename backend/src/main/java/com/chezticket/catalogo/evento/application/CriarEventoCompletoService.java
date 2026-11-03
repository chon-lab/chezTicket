package com.chezticket.catalogo.evento.application;

import com.chezticket.catalogo.evento.application.port.in.CriarEventoCompletoUseCase;
import com.chezticket.catalogo.evento.application.port.in.CriarEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.EventoCompletoCriado;
import com.chezticket.catalogo.evento.application.port.in.GerenciarCicloDeVidaDoEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;
import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.application.port.in.NovoEventoCompleto;
import com.chezticket.catalogo.evento.application.port.out.ArmazenarImagemPort;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.application.port.in.CadastrarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import com.chezticket.catalogo.sessao.application.port.in.AgendarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarEventoCompletoService implements CriarEventoCompletoUseCase {

    private final CriarEventoUseCase criarEvento;
    private final AgendarSessaoUseCase agendarSessao;
    private final CadastrarTipoIngressoUseCase cadastrarIngresso;
    private final GerenciarCicloDeVidaDoEventoUseCase cicloDeVida;
    private final ArmazenarImagemPort armazenamento;

    public CriarEventoCompletoService(
            CriarEventoUseCase criarEvento,
            AgendarSessaoUseCase agendarSessao,
            CadastrarTipoIngressoUseCase cadastrarIngresso,
            GerenciarCicloDeVidaDoEventoUseCase cicloDeVida,
            ArmazenarImagemPort armazenamento) {
        this.criarEvento = criarEvento;
        this.agendarSessao = agendarSessao;
        this.cadastrarIngresso = cadastrarIngresso;
        this.cicloDeVida = cicloDeVida;
        this.armazenamento = armazenamento;
    }

    @Override
    @Transactional
    public EventoCompletoCriado criar(NovoEventoCompleto comando) {
        String imagemCapaUrl = null;
        String imagemDivulgacaoUrl = null;

        try {
            imagemCapaUrl = armazenar(comando.imagemCapa());
            imagemDivulgacaoUrl = armazenar(comando.imagemDivulgacao());

            Evento evento = criarEvento.criar(new NovoEvento(
                    comando.organizadorId(),
                    comando.categoriaId(),
                    comando.titulo(),
                    comando.descricao(),
                    comando.classificacaoEtaria(),
                    imagemCapaUrl,
                    imagemDivulgacaoUrl,
                    comando.tags(),
                    comando.redesSociais(),
                    comando.politicaReembolso()));

            Sessao sessao = agendarSessao.agendar(NovaSessao.comEndereco(
                    evento.id(),
                    comando.endereco(),
                    comando.inicio(),
                    comando.fim()));

            List<TipoIngresso> ingressos = comando.ingressos().stream()
                    .map(ingresso -> cadastrarIngresso.cadastrar(new NovoTipoIngresso(
                            sessao.id(),
                            ingresso.nome(),
                            ingresso.gratuito(),
                            ingresso.preco(),
                            ingresso.taxaServico(),
                            ingresso.quantidadeTotal(),
                            ingresso.limitePorCompra(),
                            ingresso.inicioVendas(),
                            ingresso.fimVendas())))
                    .toList();

            if (comando.publicar()) {
                evento = cicloDeVida.publicar(evento.id());
            }

            return new EventoCompletoCriado(evento, sessao, ingressos);
        } catch (RuntimeException error) {
            excluirImagem(imagemCapaUrl, error);
            excluirImagem(imagemDivulgacaoUrl, error);
            throw error;
        }
    }

    private String armazenar(ImagemUpload imagem) {
        return imagem == null ? null : armazenamento.armazenar(imagem);
    }

    private void excluirImagem(String url, RuntimeException original) {
        if (url == null) {
            return;
        }
        try {
            armazenamento.excluir(url);
        } catch (RuntimeException cleanupError) {
            original.addSuppressed(cleanupError);
        }
    }
}
