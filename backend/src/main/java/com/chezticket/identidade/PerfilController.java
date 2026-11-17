package com.chezticket.identidade;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
class PerfilController {

    private final JdbcTemplate jdbc;
    private final IdentidadeService identidade;

    PerfilController(JdbcTemplate jdbc, IdentidadeService identidade) {
        this.jdbc = jdbc;
        this.identidade = identidade;
    }

    @GetMapping
    PerfilResponse consultar(HttpSession session) {
        long usuarioId = SessaoUsuario.exigirId(session);
        List<EventoPerfil> eventos = jdbc.query(
                "SELECT e.id, e.titulo, e.status, e.imagem_capa, MIN(s.inicio) AS inicio, "
                        + "MAX(COALESCE(l.nome, s.endereco)) AS local "
                        + "FROM evento e LEFT JOIN sessao s ON s.evento_id = e.id "
                        + "LEFT JOIN local l ON l.id = s.local_id WHERE e.organizador_id = ? "
                        + "GROUP BY e.id, e.titulo, e.status, e.imagem_capa, e.criado_em "
                        + "ORDER BY e.criado_em DESC",
                (rs, rowNum) -> new EventoPerfil(
                        rs.getLong("id"), rs.getString("titulo"), rs.getString("status"),
                        rs.getString("imagem_capa"), data(rs.getTimestamp("inicio")), rs.getString("local")),
                usuarioId);
        List<ReservaPerfilItem> reservas = jdbc.query(
                "SELECT r.id, r.status, r.expira_em, r.criada_em, e.id AS evento_id, e.titulo AS evento_titulo, "
                        + "e.imagem_capa, s.inicio, COALESCE(l.nome, s.endereco) AS local, ti.nome AS ingresso, "
                        + "ir.quantidade, ti.preco, ti.taxa_servico "
                        + "FROM reserva r JOIN item_reserva ir ON ir.reserva_id = r.id "
                        + "JOIN tipo_ingresso ti ON ti.id = ir.tipo_ingresso_id "
                        + "JOIN sessao s ON s.id = ti.sessao_id JOIN evento e ON e.id = s.evento_id "
                        + "LEFT JOIN local l ON l.id = s.local_id WHERE r.participante_id = ? "
                        + "ORDER BY r.criada_em DESC, r.id, ti.id",
                (rs, rowNum) -> new ReservaPerfilItem(
                        rs.getLong("id"), rs.getString("status"), data(rs.getTimestamp("expira_em")),
                        data(rs.getTimestamp("criada_em")), rs.getLong("evento_id"),
                        rs.getString("evento_titulo"), rs.getString("imagem_capa"),
                        data(rs.getTimestamp("inicio")), rs.getString("local"), rs.getString("ingresso"),
                        rs.getInt("quantidade"), rs.getBigDecimal("preco"), rs.getBigDecimal("taxa_servico")),
                usuarioId);
        return new PerfilResponse(identidade.buscar(usuarioId), eventos, reservas);
    }

    private LocalDateTime data(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}

record PerfilResponse(UsuarioResponse usuario, List<EventoPerfil> eventos, List<ReservaPerfilItem> reservas) {
}

record EventoPerfil(long id, String titulo, String status, String imagemCapa, LocalDateTime inicio, String local) {
}

record ReservaPerfilItem(
        long id,
        String status,
        LocalDateTime expiraEm,
        LocalDateTime criadaEm,
        long eventoId,
        String eventoTitulo,
        String imagemCapa,
        LocalDateTime inicio,
        String local,
        String ingresso,
        int quantidade,
        BigDecimal preco,
        BigDecimal taxaServico) {
}