package com.chezticket.catalogo.cupom.adapter.in.web;

import com.chezticket.catalogo.cupom.adapter.in.web.dto.CupomResponse;
import com.chezticket.catalogo.cupom.adapter.in.web.dto.ValidarCupomRequest;
import com.chezticket.catalogo.cupom.application.port.in.ValidarCupomUseCase;
import com.chezticket.identidade.SessaoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cupons", description = "Validação de cupons promocionais de eventos")
@RestController
@RequestMapping("/api/cupons")
class CupomController {

    private final ValidarCupomUseCase validarCupom;

    CupomController(ValidarCupomUseCase validarCupom) {
        this.validarCupom = validarCupom;
    }

    @Operation(summary = "Valida um cupom para um evento")
    @PostMapping("/validacao")
    CupomResponse validar(@Valid @RequestBody ValidarCupomRequest request, HttpSession session) {
        SessaoUsuario.exigirId(session);
        return CupomResponse.de(validarCupom.validar(request.eventoId(), request.codigo()));
    }
}
