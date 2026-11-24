package com.chezticket.vendas.cupom.adapter.in.web;

import com.chezticket.vendas.cupom.adapter.in.web.dto.CupomResponse;
import com.chezticket.vendas.cupom.adapter.in.web.dto.ValidarCupomRequest;
import com.chezticket.vendas.cupom.application.port.in.ValidarCupomUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cupons", description = "Validação de cupons de desconto (um por evento)")
@RestController
@RequestMapping("/api/cupons")
class CupomController {

    private final ValidarCupomUseCase validarCupom;

    CupomController(ValidarCupomUseCase validarCupom) {
        this.validarCupom = validarCupom;
    }

    @Operation(summary = "Valida um cupom para um evento")
    @ApiResponse(responseCode = "200", description = "Cupom válido")
    @ApiResponse(responseCode = "404", description = "Código inexistente para o evento")
    @ApiResponse(responseCode = "409", description = "Cupom expirado")
    @PostMapping("/validacao")
    CupomResponse validar(@Valid @RequestBody ValidarCupomRequest request) {
        return CupomResponse.de(validarCupom.validar(request.eventoId(), request.codigo()));
    }
}
