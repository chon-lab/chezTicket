package com.chezticket.catalogo.categoria.adapter.in.web;

import com.chezticket.catalogo.categoria.adapter.in.web.dto.AtualizarCategoriaRequest;
import com.chezticket.catalogo.categoria.adapter.in.web.dto.CategoriaResponse;
import com.chezticket.catalogo.categoria.adapter.in.web.dto.CriarCategoriaRequest;
import com.chezticket.catalogo.categoria.application.port.in.AtualizarCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.CadastrarCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.ConsultarCategoriasUseCase;
import com.chezticket.catalogo.categoria.application.port.in.GerenciarStatusDaCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.RemoverCategoriaUseCase;
import com.chezticket.catalogo.categoria.domain.Categoria;
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

/** Adaptador de entrada HTTP. Depende apenas das portas de entrada, nunca do serviço concreto. */
@Tag(name = "Categorias", description = "Categorias do catálogo de eventos")
@RestController
@RequestMapping("/api/categorias")
class CategoriaController {

    private final CadastrarCategoriaUseCase cadastrarCategoria;
    private final ConsultarCategoriasUseCase consultarCategorias;
    private final AtualizarCategoriaUseCase atualizarCategoria;
    private final GerenciarStatusDaCategoriaUseCase gerenciarStatus;
    private final RemoverCategoriaUseCase removerCategoria;

    CategoriaController(CadastrarCategoriaUseCase cadastrarCategoria,
            ConsultarCategoriasUseCase consultarCategorias,
            AtualizarCategoriaUseCase atualizarCategoria,
            GerenciarStatusDaCategoriaUseCase gerenciarStatus,
            RemoverCategoriaUseCase removerCategoria) {
        this.cadastrarCategoria = cadastrarCategoria;
        this.consultarCategorias = consultarCategorias;
        this.atualizarCategoria = atualizarCategoria;
        this.gerenciarStatus = gerenciarStatus;
        this.removerCategoria = removerCategoria;
    }

    @Operation(summary = "Lista as categorias", description = "Use apenasAtivas=true para ocultar as desativadas.")
    @GetMapping
    List<CategoriaResponse> listar(@RequestParam(defaultValue = "false") boolean apenasAtivas) {
        return consultarCategorias.listar(apenasAtivas).stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha uma categoria")
    @ApiResponse(responseCode = "404", description = "Categoria inexistente")
    @GetMapping("/{id}")
    CategoriaResponse detalhar(@PathVariable Long id) {
        return CategoriaResponse.de(consultarCategorias.buscarPorId(id));
    }

    @Operation(summary = "Cadastra uma categoria")
    @ApiResponse(responseCode = "409", description = "Já existe categoria com esse nome")
    @PostMapping
    ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CriarCategoriaRequest request) {
        Categoria categoria = cadastrarCategoria.cadastrar(request.nome());
        return ResponseEntity
                .created(URI.create("/api/categorias/" + categoria.id()))
                .body(CategoriaResponse.de(categoria));
    }

    @Operation(summary = "Renomeia uma categoria")
    @ApiResponse(responseCode = "409", description = "Já existe outra categoria com esse nome")
    @PutMapping("/{id}")
    CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarCategoriaRequest request) {
        return CategoriaResponse.de(atualizarCategoria.atualizar(id, request.nome()));
    }

    @Operation(summary = "Remove uma categoria")
    @ApiResponse(responseCode = "204", description = "Removida")
    @ApiResponse(responseCode = "409", description = "Não é possível remover: há eventos vinculados a esta categoria")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> remover(@PathVariable Long id) {
        removerCategoria.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativa uma categoria", description = "Volta a aparecer nas buscas com apenasAtivas=true.")
    @PostMapping("/{id}/ativacao")
    CategoriaResponse ativar(@PathVariable Long id) {
        return CategoriaResponse.de(gerenciarStatus.ativar(id));
    }

    @Operation(summary = "Desativa uma categoria", description = "Deixa de aparecer nas buscas com apenasAtivas=true.")
    @PostMapping("/{id}/desativacao")
    CategoriaResponse desativar(@PathVariable Long id) {
        return CategoriaResponse.de(gerenciarStatus.desativar(id));
    }
}
