package com.chezticket.catalogo.sessao.adapter.in.web;

import com.chezticket.catalogo.sessao.adapter.in.web.dto.AtualizarSessaoRequest;
import com.chezticket.catalogo.sessao.adapter.in.web.dto.CriarSessaoRequest;
import com.chezticket.catalogo.sessao.adapter.in.web.dto.SessaoResponse;
import com.chezticket.catalogo.sessao.application.port.in.AgendarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.AtualizarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.ConsultarSessoesUseCase;
import com.chezticket.catalogo.sessao.application.port.in.FiltroSessoes;
import com.chezticket.catalogo.sessao.application.port.in.GerenciarCicloDeVidaDaSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.RemoverSessaoUseCase;
import com.chezticket.catalogo.sessao.domain.Sessao;
import com.chezticket.catalogo.sessao.domain.StatusSessao;
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

@Tag(name = "Sessões", description = "Ocorrências (data/hora/local) de um evento")
@RestController
@RequestMapping("/api/sessoes")
class SessaoController {

    private final AgendarSessaoUseCase agendarSessao;
    private final AtualizarSessaoUseCase atualizarSessao;
    private final ConsultarSessoesUseCase consultarSessoes;
    private final GerenciarCicloDeVidaDaSessaoUseCase cicloDeVida;
    private final RemoverSessaoUseCase removerSessao;

    SessaoController(AgendarSessaoUseCase agendarSessao, AtualizarSessaoUseCase atualizarSessao,
            ConsultarSessoesUseCase consultarSessoes, GerenciarCicloDeVidaDaSessaoUseCase cicloDeVida,
            RemoverSessaoUseCase removerSessao) {
        this.agendarSessao = agendarSessao;
        this.atualizarSessao = atualizarSessao;
        this.consultarSessoes = consultarSessoes;
        this.cicloDeVida = cicloDeVida;
        this.removerSessao = removerSessao;
    }

    @Operation(summary = "Lista sessões", description = "Filtros opcionais por evento e status.")
    @GetMapping
    List<SessaoResponse> listar(
            @RequestParam(required = false) Long eventoId,
            @RequestParam(required = false) StatusSessao status) {
        return consultarSessoes.listar(new FiltroSessoes(eventoId, status)).stream()
                .map(SessaoResponse::de)
                .toList();
    }

    @Operation(summary = "Detalha uma sessão")
    @ApiResponse(responseCode = "404", description = "Sessão inexistente")
    @GetMapping("/{id}")
    SessaoResponse detalhar(@PathVariable Long id) {
        return SessaoResponse.de(consultarSessoes.buscarPorId(id));
    }

    @Operation(summary = "Agenda uma sessão para um evento")
    @ApiResponse(responseCode = "422", description = "Dados inválidos, evento ou local inexistente")
    @PostMapping
    ResponseEntity<SessaoResponse> criar(@Valid @RequestBody CriarSessaoRequest request) {
        Sessao sessao = agendarSessao.agendar(request.paraComando());
        return ResponseEntity
                .created(URI.create("/api/sessoes/" + sessao.id()))
                .body(SessaoResponse.de(sessao));
    }

    @Operation(summary = "Reagenda local/horário de uma sessão",
            description = "Permitido apenas enquanto a sessão estiver AGENDADA.")
    @PutMapping("/{id}")
    SessaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarSessaoRequest request) {
        return SessaoResponse.de(atualizarSessao.atualizar(id, request.paraComando()));
    }

    @Operation(summary = "Remove uma sessão", description = "Só é possível remover uma sessão AGENDADA.")
    @ApiResponse(responseCode = "204", description = "Removida")
    @ApiResponse(responseCode = "409", description = "Sessão já iniciada/encerrada/cancelada")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> remover(@PathVariable Long id) {
        removerSessao.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inicia a sessão", description = "AGENDADA → EM_ANDAMENTO.")
    @PostMapping("/{id}/inicio")
    SessaoResponse iniciar(@PathVariable Long id) {
        return SessaoResponse.de(cicloDeVida.iniciar(id));
    }

    @Operation(summary = "Encerra a sessão", description = "EM_ANDAMENTO → ENCERRADA.")
    @PostMapping("/{id}/encerramento")
    SessaoResponse encerrar(@PathVariable Long id) {
        return SessaoResponse.de(cicloDeVida.encerrar(id));
    }

    @Operation(summary = "Cancela a sessão", description = "AGENDADA ou EM_ANDAMENTO → CANCELADA.")
    @PostMapping("/{id}/cancelamento")
    SessaoResponse cancelar(@PathVariable Long id) {
        return SessaoResponse.de(cicloDeVida.cancelar(id));
    }
}
