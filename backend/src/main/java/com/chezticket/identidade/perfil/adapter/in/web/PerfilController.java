package com.chezticket.identidade.perfil.adapter.in.web;

import com.chezticket.identidade.perfil.adapter.in.web.dto.PerfilResponse;
import com.chezticket.identidade.perfil.application.port.in.ConsultarPerfilUseCase;
import com.chezticket.shared.web.SessaoAutenticacao;
import com.chezticket.shared.web.UploadUrlResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Perfil", description = "Visão agregada do usuário autenticado: dados, eventos organizados e reservas")
@RestController
@RequestMapping("/api/perfil")
class PerfilController {

    private final ConsultarPerfilUseCase consultarPerfil;
    private final UploadUrlResolver uploads;

    PerfilController(ConsultarPerfilUseCase consultarPerfil, UploadUrlResolver uploads) {
        this.consultarPerfil = consultarPerfil;
        this.uploads = uploads;
    }

    @Operation(summary = "Dados do perfil do usuário autenticado")
    @ApiResponse(responseCode = "401", description = "Sem sessão ativa")
    @GetMapping
    PerfilResponse buscar(HttpServletRequest httpRequest) {
        Long usuarioId = SessaoAutenticacao.usuarioIdObrigatorio(httpRequest);
        return PerfilResponse.de(consultarPerfil.buscar(usuarioId), uploads);
    }
}
