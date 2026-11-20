package com.chezticket.identidade;

import java.util.Locale;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
class IdentidadeService {

    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    IdentidadeService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    UsuarioResponse cadastrar(CadastroRequest request) {
        String nome = request.nome().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String cpf = request.cpf().replaceAll("\\D", "");

        if (existe("SELECT COUNT(*) FROM usuario WHERE email = ?", email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        }
        if (existe("SELECT COUNT(*) FROM participante WHERE cpf = ?", cpf)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este CPF já está cadastrado.");
        }

        jdbc.update("INSERT INTO usuario (nome, email, senha_hash) VALUES (?, ?, ?)",
                nome, email, passwordEncoder.encode(request.senha()));
        Long usuarioId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update("INSERT INTO participante (usuario_id, cpf) VALUES (?, ?)", usuarioId, cpf);
        jdbc.update("INSERT INTO organizador (usuario_id, razao_social, documento) VALUES (?, ?, ?)",
                usuarioId, nome, cpf);
        return buscar(usuarioId);
    }

    UsuarioResponse autenticar(String emailInformado, String senha) {
        String email = emailInformado.trim().toLowerCase(Locale.ROOT);
        Credencial credencial;
        try {
            credencial = jdbc.queryForObject(
                    "SELECT u.id, u.nome, u.email, u.senha_hash, p.cpf "
                            + "FROM usuario u JOIN participante p ON p.usuario_id = u.id "
                            + "WHERE u.email = ? AND u.status = 'ATIVO'",
                    (rs, rowNum) -> new Credencial(
                            rs.getLong("id"), rs.getString("nome"), rs.getString("email"),
                            rs.getString("senha_hash"), rs.getString("cpf")),
                    email);
        } catch (EmptyResultDataAccessException exception) {
            throw credenciaisInvalidas();
        }

        if (!passwordEncoder.matches(senha, credencial.senhaHash())) {
            throw credenciaisInvalidas();
        }
        return new UsuarioResponse(credencial.id(), credencial.nome(), credencial.email(), credencial.cpf());
    }

    UsuarioResponse buscar(long usuarioId) {
        try {
            return jdbc.queryForObject(
                    "SELECT u.id, u.nome, u.email, p.cpf "
                            + "FROM usuario u JOIN participante p ON p.usuario_id = u.id WHERE u.id = ?",
                    (rs, rowNum) -> new UsuarioResponse(
                            rs.getLong("id"), rs.getString("nome"), rs.getString("email"), rs.getString("cpf")),
                    usuarioId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada.");
        }
    }

    private boolean existe(String sql, Object valor) {
        return Boolean.TRUE.equals(jdbc.queryForObject(sql, Boolean.class, valor));
    }

    private ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    private record Credencial(long id, String nome, String email, String senhaHash, String cpf) {
    }
}