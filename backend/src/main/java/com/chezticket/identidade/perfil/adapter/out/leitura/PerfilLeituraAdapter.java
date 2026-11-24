package com.chezticket.identidade.perfil.adapter.out.leitura;

import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.application.port.out.TipoIngressoRepositorio;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.local.application.port.out.LocalRepositorio;
import com.chezticket.catalogo.local.domain.Local;
import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.application.port.out.SessaoRepositorio;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.identidade.perfil.application.port.out.PerfilLeituraPort;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class PerfilLeituraAdapter implements PerfilLeituraPort {

    private final EventoRepositorio eventos;
    private final SessaoRepositorio sessoes;
    private final LocalRepositorio locais;
    private final TipoIngressoRepositorio tiposIngresso;

    PerfilLeituraAdapter(EventoRepositorio eventos, SessaoRepositorio sessoes, LocalRepositorio locais,
            TipoIngressoRepositorio tiposIngresso) {
        this.eventos = eventos;
        this.sessoes = sessoes;
        this.locais = locais;
        this.tiposIngresso = tiposIngresso;
    }

    @Override
    public List<Evento> eventosDoOrganizador(Long organizadorId) {
        return eventos.buscar(new FiltroEventos(null, null, organizadorId));
    }

    @Override
    public Optional<Evento> buscarEvento(Long eventoId) {
        return eventoId == null ? Optional.empty() : eventos.buscarPorId(eventoId);
    }

    @Override
    public Optional<Sessao> primeiraSessaoDoEvento(Long eventoId) {
        return sessoes.buscar(new FiltroSessoes(eventoId, null)).stream()
                .min(Comparator.comparing(Sessao::inicio));
    }

    @Override
    public Optional<Sessao> buscarSessao(Long sessaoId) {
        return sessaoId == null ? Optional.empty() : sessoes.buscarPorId(sessaoId);
    }

    @Override
    public Optional<Local> buscarLocal(Long localId) {
        return localId == null ? Optional.empty() : locais.buscarPorId(localId);
    }

    @Override
    public Optional<TipoIngresso> buscarTipoIngresso(Long tipoIngressoId) {
        return tipoIngressoId == null ? Optional.empty() : tiposIngresso.buscarPorId(tipoIngressoId);
    }
}
