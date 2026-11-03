package com.chezticket.catalogo.evento.adapter.in.web.dto;

import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;
import com.chezticket.catalogo.evento.application.port.in.NovoEventoCompleto;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CriarEventoCompletoRequest(
        @NotNull Long organizadorId,
        @NotNull Long categoriaId,
        @NotBlank @Size(max = 160) String titulo,
        @Size(max = 5000) String descricao,
        ClassificacaoEtaria classificacaoEtaria,
        PoliticaReembolso politicaReembolso,
        @NotBlank @Size(max = 500) String endereco,
        @NotNull @Future LocalDateTime inicio,
        @NotNull @Future LocalDateTime fim,
        @Size(max = 20) List<@NotBlank @Size(max = 80) String> tags,
        Map<@NotBlank @Size(max = 20) String, @NotBlank @Size(max = 2048) String> redesSociais,
        @NotEmpty List<@Valid Ingresso> ingressos,
        boolean publicar) {

    private static final Set<String> REDES_SUPORTADAS = Set.of("instagram", "facebook", "x", "linkedin");

    @AssertTrue(message = "Informe apenas redes sociais suportadas.")
    public boolean isRedesSociaisSuportadas() {
        return redesSociais == null || REDES_SUPORTADAS.containsAll(redesSociais.keySet());
    }

    public NovoEventoCompleto paraComando(ImagemUpload imagemCapa, ImagemUpload imagemDivulgacao) {
        List<NovoEventoCompleto.NovoIngresso> novosIngressos = ingressos.stream()
                .map(ingresso -> new NovoEventoCompleto.NovoIngresso(
                        ingresso.nome(),
                        ingresso.gratuito(),
                        ingresso.preco(),
                        ingresso.taxaServico(),
                        ingresso.quantidadeTotal(),
                        ingresso.limitePorCompra(),
                        ingresso.inicioVendas(),
                        ingresso.fimVendas()))
                .toList();

        return new NovoEventoCompleto(
                organizadorId,
                categoriaId,
                titulo,
                descricao,
                classificacaoEtaria,
                politicaReembolso,
                endereco,
                inicio,
                fim,
                tags,
                redesSociais,
                novosIngressos,
                imagemCapa,
                imagemDivulgacao,
                publicar);
    }

    public record Ingresso(
            @NotBlank @Size(max = 80) String nome,
            boolean gratuito,
            @PositiveOrZero BigDecimal preco,
            @PositiveOrZero BigDecimal taxaServico,
            @Positive int quantidadeTotal,
            @Positive int limitePorCompra,
            LocalDate inicioVendas,
            LocalDate fimVendas) {
    }
}
