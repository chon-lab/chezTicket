package com.chezticket.catalogo.ingresso.adapter.in.web;

import com.chezticket.catalogo.ingresso.adapter.in.web.dto.AtualizarTipoIngressoRequest;
import com.chezticket.catalogo.ingresso.adapter.in.web.dto.CriarTipoIngressoRequest;
import com.chezticket.catalogo.ingresso.adapter.in.web.dto.TipoIngressoResponse;
import com.chezticket.catalogo.ingresso.application.port.in.AtualizarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.CadastrarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.ConsultarTiposIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.RemoverTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tipos de ingresso", description = "Tipos de ingresso vendidos em cada sessão, com controle de estoque")
@RestController
@RequestMapping("/api/tipos-ingresso")
class TipoIngressoController {

    private final CadastrarTipoIngressoUseCase cadastrarTipoIngresso;
    private final AtualizarTipoIngressoUseCase atualizarTipoIngresso;
    private final ConsultarTiposIngressoUseCase consultarTiposIngresso;
    private final RemoverTipoIngressoUseCase removerTipoIngresso;

    TipoIngressoController(CadastrarTipoIngressoUseCase cadastrarTipoIngresso,
            AtualizarTipoIngressoUseCase atualizarTipoIngresso,
            ConsultarTiposIngressoUseCase consultarTiposIngresso,
            RemoverTipoIngressoUseCase removerTipoIngresso) {
        this.cadastrarTipoIngresso = cadastrarTipoIngresso;
        this.atualizarTipoIngresso = atualizarTipoIngresso;
        this.consultarTiposIngresso = consultarTiposIngresso;
        this.removerTipoIngresso = removerTipoIngresso;
    }

    @Operation(summary = "Lista os tipos de ingresso de uma sessão")
    @GetMapping
    List<TipoIngressoResponse> listar(@RequestParam Long sessaoId) {
        return consultarTiposIngresso.listarPorSessao(sessaoId).stream()
                .map(TipoIngressoResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha um tipo de ingresso")
    @ApiResponse(responseCode = "404", description = "Tipo de ingresso inexistente")
    @GetMapping("/{id}")
    TipoIngressoResponse detalhar(@PathVariable Long id) {
        return TipoIngressoResponse.de(consultarTiposIngresso.buscarPorId(id));
    }

    @Operation(summary = "Cadastra um tipo de ingresso para uma sessão")
    @ApiResponse(responseCode = "422", description = "Dados inválidos ou sessão inexistente")
    @PostMapping
    ResponseEntity<TipoIngressoResponse> criar(@Valid @RequestBody CriarTipoIngressoRequest request) {
        TipoIngresso tipoIngresso = cadastrarTipoIngresso.cadastrar(request.paraComando());
        return ResponseEntity
                .created(URI.create("/api/tipos-ingresso/" + tipoIngresso.id()))
                .body(TipoIngressoResponse.de(tipoIngresso));
    }

    @Operation(summary = "Atualiza os dados de um tipo de ingresso",
            description = "A nova quantidade total não pode ser menor que a quantidade já vendida/reservada.")
    @PutMapping("/{id}")
    TipoIngressoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarTipoIngressoRequest request) {
        return TipoIngressoResponse.de(atualizarTipoIngresso.atualizar(id, request.paraComando()));
    }

    @Operation(summary = "Remove um tipo de ingresso",
            description = "Só é possível remover um tipo sem nenhuma unidade vendida/reservada.")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(responseCode = "409", description = "Há unidades vendidas/reservadas")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> remover(@PathVariable Long id) {
        removerTipoIngresso.remover(id);
        return ResponseEntity.noContent().build();
    }
}
