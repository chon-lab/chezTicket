package com.chezticket.catalogo.evento.adapter.out.persistence;

import com.chezticket.catalogo.evento.application.port.in.FiltroEventos;
import com.chezticket.catalogo.evento.application.port.out.EventoRepositorio;
import com.chezticket.catalogo.evento.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class EventoPersistenceAdapter implements EventoRepositorio {

    private static final String PEDIDOS_DO_EVENTO = """
            SELECT ip.pedido_id
            FROM item_pedido ip
            JOIN tipo_ingresso ti ON ti.id = ip.tipo_ingresso_id
            JOIN sessao s ON s.id = ti.sessao_id
            WHERE s.evento_id = ?
            UNION
            SELECT i.pedido_id
            FROM ingresso i
            JOIN tipo_ingresso ti ON ti.id = i.tipo_ingresso_id
            JOIN sessao s ON s.id = ti.sessao_id
            WHERE s.evento_id = ?
            """;

    private static final String RESERVAS_DO_EVENTO = """
            SELECT DISTINCT ir.reserva_id
            FROM item_reserva ir
            JOIN tipo_ingresso ti ON ti.id = ir.tipo_ingresso_id
            JOIN sessao s ON s.id = ti.sessao_id
            WHERE s.evento_id = ?
            """;

    private final EventoJpaRepository jpa;
    private final JdbcTemplate jdbc;

    EventoPersistenceAdapter(EventoJpaRepository jpa, JdbcTemplate jdbc) {
        this.jpa = jpa;
        this.jdbc = jdbc;
    }

    @Override
    public Evento salvar(Evento evento) {
        return EventoPersistenceMapper.paraDominio(
                jpa.save(EventoPersistenceMapper.paraEntidade(evento)));
    }

    @Override
    public Optional<Evento> buscarPorId(Long id) {
        return jpa.findById(id).map(EventoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Evento> buscar(FiltroEventos filtro) {
        return jpa.buscar(filtro.status(), filtro.categoriaId(), filtro.organizadorId()).stream()
                .map(EventoPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(Long id) {
        List<Long> pedidos = jdbc.queryForList(PEDIDOS_DO_EVENTO, Long.class, id, id);
        List<Long> reservas = jdbc.queryForList(RESERVAS_DO_EVENTO, Long.class, id);

        for (Long pedidoId : pedidos) {
            jdbc.update("DELETE FROM pedido WHERE id = ?", pedidoId);
        }
        for (Long reservaId : reservas) {
            jdbc.update("DELETE FROM reserva WHERE id = ?", reservaId);
        }

        jpa.deleteById(id);
    }
}
