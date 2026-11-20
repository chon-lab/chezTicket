package com.chezticket.vendas.reserva.adapter.in.web;

import com.chezticket.identidade.SessaoUsuario;
import com.chezticket.vendas.reserva.adapter.in.web.dto.CriarReservaRequest;
import com.chezticket.vendas.reserva.adapter.in.web.dto.ReservaResponse;
import com.chezticket.vendas.reserva.application.port.in.CancelarReservaUseCase;
import com.chezticket.vendas.reserva.application.port.in.ConsultarReservasUseCase;
import com.chezticket.vendas.reserva.application.port.in.ReservarIngressosUseCase;
import com.chezticket.vendas.reserva.domain.Reserva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @Operation(summary = "Lista as reservas do participante autenticado")
    @GetMapping
    List<ReservaResponse> listar(HttpSession session) {
        long participanteId = SessaoUsuario.exigirId(session);
        return consultarReservas.listarPorParticipante(participanteId).stream()
                .map(ReservaResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha uma reserva")
    @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    @GetMapping("/{id}")
    ReservaResponse detalhar(@PathVariable Long id, HttpSession session) {
        return ReservaResponse.de(exigirDono(id, session));
    }

    @Operation(summary = "Reserva um ou mais tipos de ingresso",
            description = "Retém o estoque por 10 minutos (constante do serviço). "
                    + "Se a reserva não virar pedido até lá, ela expira e o estoque volta a ficar disponível.")
    @ApiResponse(responseCode = "201", description = "Reserva criada")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente para algum item")
    @ApiResponse(responseCode = "422", description = "Tipo de ingresso inexistente ou dados inválidos")
    @PostMapping
    ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody CriarReservaRequest request, HttpSession session) {
        long participanteAutenticado = SessaoUsuario.exigirId(session);
        Reserva reserva = reservarIngressos.reservar(request.paraComando(participanteAutenticado));
        return ResponseEntity
                .created(URI.create("/api/reservas/" + reserva.id()))
                .body(ReservaResponse.de(reserva));
    }

    @Operation(summary = "Cancela uma reserva ativa", description = "Libera imediatamente o estoque retido.")
    @ApiResponse(responseCode = "409", description = "A reserva não está mais ATIVA")
    @PostMapping("/{id}/cancelamento")
    ReservaResponse cancelar(@PathVariable Long id, HttpSession session) {
        exigirDono(id, session);
        return ReservaResponse.de(cancelarReserva.cancelar(id));
    }

    private Reserva exigirDono(Long reservaId, HttpSession session) {
        long usuarioId = SessaoUsuario.exigirId(session);
        Reserva reserva = consultarReservas.buscarPorId(reservaId);
        if (!Objects.equals(reserva.participanteId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reserva pertence a outra conta.");
        }
        return reserva;
    }
}
