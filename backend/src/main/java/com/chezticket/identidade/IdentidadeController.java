package com.chezticket.identidade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class IdentidadeController {

    private final IdentidadeService identidade;

    IdentidadeController(IdentidadeService identidade) {
        this.identidade = identidade;
    }

    @PostMapping("/cadastro")
    UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest request, HttpServletRequest servletRequest) {
        UsuarioResponse usuario = identidade.cadastrar(request);
        iniciarSessao(servletRequest, usuario.id());
        return usuario;
    }

    @PostMapping("/login")
    UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        UsuarioResponse usuario = identidade.autenticar(request.email(), request.senha());
        iniciarSessao(servletRequest, usuario.id());
        return usuario;
    }

    @GetMapping("/me")
    UsuarioResponse atual(HttpSession session) {
        return identidade.buscar(SessaoUsuario.exigirId(session));
    }

    @PostMapping("/logout")
    ResponseEntity<Map<String, Boolean>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("loggedOut", true));
    }

    private void iniciarSessao(HttpServletRequest request, long usuarioId) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        SessaoUsuario.iniciar(request.getSession(true), usuarioId);
    }
}

record CadastroRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(min = 8, max = 72) String senha,
        @NotBlank @Pattern(regexp = "(?:[0-9]{11}|[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2})") String cpf) {
}

record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 72) String senha) {
}

record UsuarioResponse(long id, String nome, String email, String cpf) {
}