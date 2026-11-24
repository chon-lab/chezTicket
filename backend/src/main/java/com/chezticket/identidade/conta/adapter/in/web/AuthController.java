package com.chezticket.identidade.conta.adapter.in.web;

import com.chezticket.identidade.conta.adapter.in.web.dto.CadastroRequest;
import com.chezticket.identidade.conta.adapter.in.web.dto.LoginRequest;
import com.chezticket.identidade.conta.adapter.in.web.dto.UsuarioResponse;
import com.chezticket.identidade.conta.application.port.in.AutenticarUseCase;
import com.chezticket.identidade.conta.application.port.in.CadastrarUsuarioUseCase;
import com.chezticket.identidade.conta.application.port.in.ConsultarUsuarioUseCase;
import com.chezticket.identidade.conta.domain.Usuario;
import com.chezticket.shared.web.SessaoAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autenticação simples por sessão de servlet (cookie {@code JSESSIONID}), sem JWT — conforme
 * decidido no plano de integração com o front. Toda conta cadastrada vira participante e
 * organizador ao mesmo tempo (ver {@link com.chezticket.identidade.conta.domain.Usuario}).
 */
@Tag(name = "Autenticação", description = "Cadastro, login e sessão do usuário (participante + organizador)")
@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final CadastrarUsuarioUseCase cadastrarUsuario;
    private final AutenticarUseCase autenticar;
    private final ConsultarUsuarioUseCase consultarUsuario;

    AuthController(CadastrarUsuarioUseCase cadastrarUsuario, AutenticarUseCase autenticar,
            ConsultarUsuarioUseCase consultarUsuario) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.autenticar = autenticar;
        this.consultarUsuario = consultarUsuario;
    }

    @Operation(summary = "Cadastra uma conta nova", description = "Cria usuário + participante + organizador juntos.")
    @ApiResponse(responseCode = "200", description = "Conta criada e sessão iniciada")
    @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrado")
    @PostMapping("/cadastro")
    UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest request, HttpServletRequest httpRequest) {
        Usuario usuario = cadastrarUsuario.cadastrar(request.paraComando());
        SessaoAutenticacao.autenticar(httpRequest, usuario.id());
        return UsuarioResponse.de(usuario);
    }

    @Operation(summary = "Autentica com e-mail e senha")
    @ApiResponse(responseCode = "200", description = "Sessão iniciada")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    @PostMapping("/login")
    UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Usuario usuario = autenticar.autenticar(request.email(), request.senha());
        SessaoAutenticacao.autenticar(httpRequest, usuario.id());
        return UsuarioResponse.de(usuario);
    }

    @Operation(summary = "Encerra a sessão atual")
    @PostMapping("/logout")
    Map<String, Boolean> logout(HttpServletRequest httpRequest) {
        SessaoAutenticacao.encerrar(httpRequest);
        return Map.of("loggedOut", true);
    }

    @Operation(summary = "Dados da sessão atual", description = "401 quando não há sessão autenticada.")
    @ApiResponse(responseCode = "200", description = "Autenticado")
    @ApiResponse(responseCode = "401", description = "Sem sessão ativa")
    @GetMapping("/me")
    UsuarioResponse me(HttpServletRequest httpRequest) {
        Long usuarioId = SessaoAutenticacao.usuarioIdObrigatorio(httpRequest);
        return UsuarioResponse.de(consultarUsuario.buscarPorId(usuarioId));
    }
}
