package com.chezticket.vendas.pedido.adapter.in.web;

import com.chezticket.vendas.pedido.adapter.in.web.dto.CriarPedidoRequest;
import com.chezticket.vendas.pedido.adapter.in.web.dto.PedidoResponse;
import com.chezticket.vendas.pedido.adapter.in.web.dto.ResultadoPedidoResponse;
import com.chezticket.vendas.pedido.application.port.in.ConsultarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.CriarPedidoUseCase;
import com.chezticket.vendas.pedido.application.port.in.ResultadoPedido;
import com.chezticket.shared.web.SessaoAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pedidos", description = "Conversão de reservas em pedidos e acompanhamento da compra")
@RestController
@RequestMapping("/api/pedidos")
class PedidoController {

    private final CriarPedidoUseCase criarPedido;
    private final ConsultarPedidoUseCase consultarPedido;

    PedidoController(CriarPedidoUseCase criarPedido, ConsultarPedidoUseCase consultarPedido) {
        this.criarPedido = criarPedido;
        this.consultarPedido = consultarPedido;
    }

    @Operation(summary = "Lista os pedidos do participante autenticado")
    @ApiResponse(responseCode = "401", description = "Sem sessão ativa")
    @GetMapping
    List<PedidoResponse> listar(HttpServletRequest httpRequest) {
        Long participanteId = SessaoAutenticacao.usuarioIdObrigatorio(httpRequest);
        return consultarPedido.listarPorParticipante(participanteId).stream()
                .map(PedidoResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha um pedido")
    @ApiResponse(responseCode = "404", description = "Pedido inexistente")
    @GetMapping("/{id}")
    PedidoResponse detalhar(@PathVariable Long id) {
        return PedidoResponse.de(consultarPedido.buscarPorId(id));
    }

    @Operation(summary = "Converte uma reserva ativa em pedido",
            description = "Se o total for zero (ingressos gratuitos), confirma e emite os ingressos na hora. "
                    + "Senão, abre uma sessão de checkout Stripe e devolve a URL de pagamento.")
    @ApiResponse(responseCode = "201", description = "Pedido criado")
    @ApiResponse(responseCode = "401", description = "Sem sessão ativa")
    @ApiResponse(responseCode = "404", description = "Reserva ou cupom inexistente")
    @ApiResponse(responseCode = "409", description = "Falha ao abrir o checkout no gateway de pagamento")
    @ApiResponse(responseCode = "422", description = "Reserva não pertence ao participante ou não está mais ativa")
    @PostMapping
    ResponseEntity<ResultadoPedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest request,
            HttpServletRequest httpRequest) {
        Long participanteId = SessaoAutenticacao.usuarioIdObrigatorio(httpRequest);
        ResultadoPedido resultado = criarPedido.criar(request.paraComando(participanteId));
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + resultado.pedido().id()))
                .body(ResultadoPedidoResponse.de(resultado));
    }
}
