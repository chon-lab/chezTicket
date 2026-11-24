package com.chezticket.identidade.perfil.application.port.out;

import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.local.domain.Local;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída: leitura entre módulos usada só para montar o perfil agregado (eventos
 * organizados + reservas do participante). É uma composição de leitura, não uma regra de
 * negócio — por isso um único port agrupa as consultas, em vez de um por módulo like
 * {@code VerificarCategoriaPort} e companhia.
 */
public interface PerfilLeituraPort {

    List<Evento> eventosDoOrganizador(Long organizadorId);

    Optional<Evento> buscarEvento(Long eventoId);

    Optional<Sessao> primeiraSessaoDoEvento(Long eventoId);

    Optional<Sessao> buscarSessao(Long sessaoId);

    Optional<Local> buscarLocal(Long localId);

    Optional<TipoIngresso> buscarTipoIngresso(Long tipoIngressoId);
}
