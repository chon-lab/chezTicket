package com.chezticket.catalogo.eventocompleto.application;

import com.chezticket.catalogo.eventocompleto.application.port.in.CriarEventoCompletoUseCase;
import com.chezticket.catalogo.evento.application.port.in.CriarEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.GerenciarCicloDeVidaDoEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.application.port.in.CadastrarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.sessao.application.port.in.AgendarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.vendas.cupom.application.port.in.CriarCupomUseCase;
import com.chezticket.vendas.cupom.application.port.in.NovoCupom;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquestra, numa única transação, o que o assistente de criação de evento do front faz em um
 * só envio: evento + sessão (endereço livre, sem local estruturado) + tipos de ingresso + cupom
 * opcional, publicando ao final se pedido. Depende diretamente dos casos de uso públicos de
 * cada módulo — é uma composição explícita de workflow, não um acoplamento de domínio.
 */
@Service
@Transactional
public class EventoCompletoService implements CriarEventoCompletoUseCase {

    private final CriarEventoUseCase criarEvento;
    private final GerenciarCicloDeVidaDoEventoUseCase cicloDeVidaDoEvento;
    private final AgendarSessaoUseCase agendarSessao;
    private final CadastrarTipoIngressoUseCase cadastrarTipoIngresso;
    private final CriarCupomUseCase criarCupom;

    public EventoCompletoService(CriarEventoUseCase criarEvento,
            GerenciarCicloDeVidaDoEventoUseCase cicloDeVidaDoEvento,
            AgendarSessaoUseCase agendarSessao,
            CadastrarTipoIngressoUseCase cadastrarTipoIngresso,
            CriarCupomUseCase criarCupom) {
        this.criarEvento = criarEvento;
        this.cicloDeVidaDoEvento = cicloDeVidaDoEvento;
        this.agendarSessao = agendarSessao;
        this.cadastrarTipoIngresso = cadastrarTipoIngresso;
        this.criarCupom = criarCupom;
    }

    @Override
    public EventoCompletoResultado criar(NovoEventoCompleto comando) {
        Evento evento = criarEvento.criar(new NovoEvento(
                comando.organizadorId(),
                comando.categoriaId(),
                comando.titulo(),
                comando.descricao(),
                comando.classificacaoEtaria(),
                comando.imagemCapa(),
                comando.imagemDivulgacao(),
                comando.politicaReembolso(),
                comando.tags(),
                comando.redesSociais()));

        Sessao sessao = agendarSessao.agendar(new NovaSessao(
                evento.id(), null, comando.endereco(), comando.inicio(), comando.fim()));

        List<TipoIngresso> ingressos = comando.ingressos().stream()
                .map(ingresso -> cadastrarTipoIngresso.cadastrar(new NovoTipoIngresso(
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

        if (comando.cupom() != null) {
            criarCupom.criar(new NovoCupom(
                    evento.id(), comando.cupom().codigo(), comando.cupom().percentualDesconto(),
                    sessao.fim().toLocalDate()));
        }

        Evento eventoFinal = comando.publicar() ? cicloDeVidaDoEvento.publicar(evento.id()) : evento;

        return new EventoCompletoResultado(eventoFinal, sessao, ingressos);
    }
}
