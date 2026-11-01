package com.chezticket.vendas.reserva.adapter.in.web;

import com.chezticket.vendas.reserva.adapter.in.web.dto.CriarReservaRequest;
import com.chezticket.vendas.reserva.adapter.in.web.dto.ReservaResponse;
import com.chezticket.vendas.reserva.application.port.in.CancelarReservaUseCase;
import com.chezticket.vendas.reserva.application.port.in.ConsultarReservasUseCase;
import com.chezticket.vendas.reserva.application.port.in.ReservarIngressosUseCase;
import com.chezticket.vendas.reserva.domain.Reserva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reservas", description = "Seleção e retenção temporária de ingressos antes da compra")
@RestController
@RequestMapping("/api/reservas")
class ReservaController {

    private final ReservarIngressosUseCase reservarIngressos;
    private final ConsultarReservasUseCase consultarReservas;
    private final CancelarReservaUseCase cancelarReserva;

    ReservaController(ReservarIngressosUseCase reservarIngressos, ConsultarReservasUseCase consultarReservas,
            CancelarReservaUseCase cancelarReserva) {
        this.reservarIngressos = reservarIngressos;
        this.consultarReservas = consultarReservas;
        this.cancelarReserva = cancelarReserva;
    }

    @Operation(summary = "Lista as reservas de um participante")
    @GetMapping
    List<ReservaResponse> listar(@RequestParam Long participanteId) {
        return consultarReservas.listarPorParticipante(participanteId).stream()
                .map(ReservaResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha uma reserva")
    @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    @GetMapping("/{id}")
    ReservaResponse detalhar(@PathVariable Long id) {
        return ReservaResponse.de(consultarReservas.buscarPorId(id));
    }

    @Operation(summary = "Reserva um ou mais tipos de ingresso",
            description = "Retém o estoque por 10 minutos (constante do serviço). "
                    + "Se a reserva não virar pedido até lá, ela expira e o estoque volta a ficar disponível.")
    @ApiResponse(responseCode = "201", description = "Reserva criada")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente para algum item")
    @ApiResponse(responseCode = "422", description = "Participante ou tipo de ingresso inexistente")
    @PostMapping
    ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody CriarReservaRequest request) {
        Reserva reserva = reservarIngressos.reservar(request.paraComando());
        return ResponseEntity
                .created(URI.create("/api/reservas/" + reserva.id()))
                .body(ReservaResponse.de(reserva));
    }

    @Operation(summary = "Cancela uma reserva ativa", description = "Libera imediatamente o estoque retido.")
    @ApiResponse(responseCode = "409", description = "A reserva não está mais ATIVA")
    @PostMapping("/{id}/cancelamento")
    ReservaResponse cancelar(@PathVariable Long id) {
        return ReservaResponse.de(cancelarReserva.cancelar(id));
    }
}
