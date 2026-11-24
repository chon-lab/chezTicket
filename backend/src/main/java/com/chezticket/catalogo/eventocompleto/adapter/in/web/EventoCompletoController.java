package com.chezticket.catalogo.eventocompleto.adapter.in.web;

import com.chezticket.catalogo.eventocompleto.adapter.in.web.dto.CriarEventoCompletoRequest;
import com.chezticket.catalogo.eventocompleto.adapter.in.web.dto.EventoCompletoResponse;
import com.chezticket.catalogo.eventocompleto.application.EventoCompletoResultado;
import com.chezticket.catalogo.eventocompleto.application.port.in.CriarEventoCompletoUseCase;
import com.chezticket.shared.upload.ArmazenamentoDeArquivoPort;
import com.chezticket.shared.web.SessaoAutenticacao;
import com.chezticket.shared.web.UploadUrlResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Atalho usado pelo assistente de criação de evento do front: cria evento, sessão (com endereço
 * livre), tipos de ingresso e cupom opcional numa chamada só, com upload de imagens.
 * Equivalente a usar {@code /api/eventos}, {@code /api/sessoes}, {@code /api/tipos-ingresso} e
 * {@code /api/cupons} separadamente — mantidos para quem usa um Local estruturado (Fase 4).
 */
@Tag(name = "Eventos", description = "Cadastro e ciclo de vida dos eventos do catálogo")
@RestController
@RequestMapping("/api/eventos")
class EventoCompletoController {

    private final CriarEventoCompletoUseCase criarEventoCompleto;
    private final ArmazenamentoDeArquivoPort armazenamento;
    private final UploadUrlResolver uploads;

    EventoCompletoController(CriarEventoCompletoUseCase criarEventoCompleto,
            ArmazenamentoDeArquivoPort armazenamento, UploadUrlResolver uploads) {
        this.criarEventoCompleto = criarEventoCompleto;
        this.armazenamento = armazenamento;
        this.uploads = uploads;
    }

    @Operation(summary = "Cria um evento completo (evento + sessão + ingressos + cupom opcional)",
            description = "Multipart: parte 'dados' (JSON) + 'imagemCapa'/'imagemDivulgacao' (arquivos, opcionais). "
                    + "O organizador é sempre o usuário autenticado.")
    @ApiResponse(responseCode = "201", description = "Evento criado")
    @ApiResponse(responseCode = "401", description = "Sem sessão ativa")
    @ApiResponse(responseCode = "422", description = "Dados inválidos ou categoria inexistente")
    @PostMapping(value = "/completo", consumes = "multipart/form-data")
    ResponseEntity<EventoCompletoResponse> criarCompleto(
            @RequestPart("dados") @Valid CriarEventoCompletoRequest dados,
            @RequestPart(value = "imagemCapa", required = false) MultipartFile imagemCapa,
            @RequestPart(value = "imagemDivulgacao", required = false) MultipartFile imagemDivulgacao,
            HttpServletRequest httpRequest) {
        Long organizadorId = SessaoAutenticacao.usuarioIdObrigatorio(httpRequest);
        String caminhoCapa = salvarSeEnviado(imagemCapa);
        String caminhoDivulgacao = salvarSeEnviado(imagemDivulgacao);

        EventoCompletoResultado resultado = criarEventoCompleto.criar(
                dados.paraComando(organizadorId, caminhoCapa, caminhoDivulgacao));

        return ResponseEntity
                .created(URI.create("/api/eventos/" + resultado.evento().id()))
                .body(EventoCompletoResponse.de(resultado, uploads));
    }

    private String salvarSeEnviado(MultipartFile arquivo) {
        return (arquivo != null && !arquivo.isEmpty()) ? armazenamento.salvar(arquivo) : null;
    }
}
