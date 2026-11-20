package com.chezticket.identidade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class IdentidadeServiceTest {

    @Mock
    JdbcTemplate jdbc;

    private IdentidadeService service;

    @BeforeEach
    void setUp() {
        service = new IdentidadeService(jdbc);
    }

    @Test
    void cadastraParticipanteEOrganizadorComSenhaCriptografada() {
        UsuarioResponse usuario = new UsuarioResponse(42L, "Ana Silva", "ana@example.com", "12345678909");
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), any())).thenReturn(false);
        when(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class)).thenReturn(42L);
        when(jdbc.queryForObject(
                startsWith("SELECT u.id, u.nome, u.email, p.cpf"),
                ArgumentMatchers.<RowMapper<UsuarioResponse>>any(),
                eq(42L)))
                .thenReturn(usuario);

        UsuarioResponse result = service.cadastrar(
                new CadastroRequest(" Ana Silva ", "ANA@example.com", "senha-forte-123", "123.456.789-09"));

        assertThat(result).isEqualTo(usuario);
        verify(jdbc).update(
                eq("INSERT INTO usuario (nome, email, senha_hash) VALUES (?, ?, ?)"),
                eq("Ana Silva"), eq("ana@example.com"),
                org.mockito.ArgumentMatchers.argThat((String hash) -> hash.startsWith("$2a$")
                        && !hash.equals("senha-forte-123")));
        verify(jdbc).update("INSERT INTO participante (usuario_id, cpf) VALUES (?, ?)", 42L, "12345678909");
        verify(jdbc).update(
                "INSERT INTO organizador (usuario_id, razao_social, documento) VALUES (?, ?, ?)",
                42L, "Ana Silva", "12345678909");
    }
}
