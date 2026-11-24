package com.chezticket.identidade.perfil.application;

import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.identidade.conta.application.port.out.UsuarioRepositorio;
import com.chezticket.identidade.conta.domain.Usuario;
import com.chezticket.identidade.perfil.application.port.in.ConsultarPerfilUseCase;
import com.chezticket.identidade.perfil.application.port.out.PerfilLeituraPort;
import com.chezticket.shared.domain.NaoAutenticadoException;
import com.chezticket.vendas.reserva.application.port.out.ReservaRepositorio;
import com.chezticket.vendas.reserva.domain.ItemReserva;
import com.chezticket.vendas.reserva.domain.Reserva;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PerfilService implements ConsultarPerfilUseCase {

    private final UsuarioRepositorio usuarios;
    private final ReservaRepositorio reservas;
    private final PerfilLeituraPort leitura;

    public PerfilService(UsuarioRepositorio usuarios, ReservaRepositorio reservas, PerfilLeituraPort leitura) {
        this.usuarios = usuarios;
        this.reservas = reservas;
        this.leitura = leitura;
    }

    @Override
    public PerfilAgregado buscar(Long usuarioId) {
        Usuario usuario = usuarios.buscarPorId(usuarioId)
                .orElseThrow(() -> new NaoAutenticadoException("Sessão inválida ou expirada."));

        var eventosOrganizados = leitura.eventosDoOrganizador(usuarioId).stream()
                .map(this::paraEventoOrganizado)
                .toList();

        var itensDeReserva = reservas.listarPorParticipante(usuarioId).stream()
                .flatMap(reserva -> reserva.itens().stream().map(item -> paraItemDeReserva(reserva, item)))
                .toList();

        return new PerfilAgregado(usuario, eventosOrganizados, itensDeReserva);
    }

    private EventoOrganizadoPerfil paraEventoOrganizado(Evento evento) {
        Sessao primeiraSessao = leitura.primeiraSessaoDoEvento(evento.id()).orElse(null);
        return new EventoOrganizadoPerfil(
                evento.id(),
                evento.titulo(),
                evento.status(),
                evento.imagemCapa(),
                primeiraSessao != null ? primeiraSessao.inicio() : null,
                primeiraSessao != null ? descricaoDoLocal(primeiraSessao) : null);
    }

    private ItemDeReservaPerfil paraItemDeReserva(Reserva reserva, ItemReserva item) {
        TipoIngresso tipo = leitura.buscarTipoIngresso(item.tipoIngressoId()).orElse(null);
        Sessao sessao = tipo != null ? leitura.buscarSessao(tipo.sessaoId()).orElse(null) : null;
        Evento evento = sessao != null ? leitura.buscarEvento(sessao.eventoId()).orElse(null) : null;

        return new ItemDeReservaPerfil(
                reserva.id(),
                reserva.status(),
                reserva.expiraEm(),
                reserva.criadaEm(),
                evento != null ? evento.id() : null,
                evento != null ? evento.titulo() : "Evento removido",
                evento != null ? evento.imagemCapa() : null,
                sessao != null ? sessao.inicio() : null,
                sessao != null ? descricaoDoLocal(sessao) : null,
                tipo != null ? tipo.nome() : "Ingresso removido",
                item.quantidade(),
                tipo != null ? tipo.preco() : BigDecimal.ZERO,
                tipo != null ? tipo.taxaServico() : BigDecimal.ZERO);
    }

    private String descricaoDoLocal(Sessao sessao) {
        if (sessao.endereco() != null) {
            return sessao.endereco();
        }
        return leitura.buscarLocal(sessao.localId()).map(local -> local.nome()).orElse(null);
    }
}
