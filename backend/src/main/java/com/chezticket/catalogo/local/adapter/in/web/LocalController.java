package com.chezticket.catalogo.local.adapter.in.web;

import com.chezticket.catalogo.local.adapter.in.web.dto.AtualizarLocalRequest;
import com.chezticket.catalogo.local.adapter.in.web.dto.CriarLocalRequest;
import com.chezticket.catalogo.local.adapter.in.web.dto.LocalResponse;
import com.chezticket.catalogo.local.application.port.in.AtualizarLocalUseCase;
import com.chezticket.catalogo.local.application.port.in.CadastrarLocalUseCase;
import com.chezticket.catalogo.local.application.port.in.ConsultarLocaisUseCase;
import com.chezticket.catalogo.local.application.port.in.RemoverLocalUseCase;
import com.chezticket.catalogo.local.domain.Local;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Locais", description = "Locais (venues) onde as sessões de eventos acontecem")
@RestController
@RequestMapping("/api/locais")
class LocalController {

    private final CadastrarLocalUseCase cadastrarLocal;
    private final ConsultarLocaisUseCase consultarLocais;
    private final AtualizarLocalUseCase atualizarLocal;
    private final RemoverLocalUseCase removerLocal;

    LocalController(CadastrarLocalUseCase cadastrarLocal, ConsultarLocaisUseCase consultarLocais,
            AtualizarLocalUseCase atualizarLocal, RemoverLocalUseCase removerLocal) {
        this.cadastrarLocal = cadastrarLocal;
        this.consultarLocais = consultarLocais;
        this.atualizarLocal = atualizarLocal;
        this.removerLocal = removerLocal;
    }

    @Operation(summary = "Lista os locais cadastrados")
    @GetMapping
    List<LocalResponse> listar() {
        return consultarLocais.listar().stream()
                .map(LocalResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha um local")
    @ApiResponse(responseCode = "404", description = "Local inexistente")
    @GetMapping("/{id}")
    LocalResponse detalhar(@PathVariable Long id) {
        return LocalResponse.de(consultarLocais.buscarPorId(id));
    }

    @Operation(summary = "Cadastra um local")
    @PostMapping
    ResponseEntity<LocalResponse> criar(@Valid @RequestBody CriarLocalRequest request) {
        Local local = cadastrarLocal.cadastrar(request.paraComando());
        return ResponseEntity
                .created(URI.create("/api/locais/" + local.id()))
                .body(LocalResponse.de(local));
    }

    @Operation(summary = "Atualiza os dados de um local")
    @PutMapping("/{id}")
    LocalResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarLocalRequest request) {
        return LocalResponse.de(atualizarLocal.atualizar(id, request.paraComando()));
    }

    @Operation(summary = "Remove um local",
            description = "Só é possível remover um local que não esteja referenciado por nenhuma sessão.")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(responseCode = "409", description = "Há sessões vinculadas a este local")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> remover(@PathVariable Long id) {
        removerLocal.remover(id);
        return ResponseEntity.noContent().build();
    }
}
