package com.chezticket.catalogo.evento.adapter.in.web;

import com.chezticket.catalogo.evento.application.port.in.ConsultarEventosUseCase;
import com.chezticket.catalogo.evento.domain.Evento;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/eventos")
class EventoCompartilhamentoController {

    private final ConsultarEventosUseCase consultarEventos;
    private final String frontendUrl;
    private final String publicApiUrl;

    EventoCompartilhamentoController(
            ConsultarEventosUseCase consultarEventos,
            @Value("${chezticket.frontend-url:http://localhost:5173}") String frontendUrl,
            @Value("${chezticket.public-api-url:http://localhost:8081}") String publicApiUrl) {
        this.consultarEventos = consultarEventos;
        this.frontendUrl = removerBarraFinal(frontendUrl);
        this.publicApiUrl = removerBarraFinal(publicApiUrl);
    }

    @GetMapping(path = "/{id}/compartilhar", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> compartilhar(@PathVariable Long id) {
        Evento evento = consultarEventos.buscarPorId(id);
        String titulo = evento.titulo();
        String descricao = evento.descricao() == null || evento.descricao().isBlank()
                ? "Confira este evento no ChezTicket."
                : evento.descricao().replaceAll("\\s+", " ").trim();
        String imagemUrl = urlImagem(evento.imagemDivulgacao());
        if (imagemUrl == null) {
            imagemUrl = urlImagem(evento.imagemCapa());
        }

        String linkEvento = frontendUrl + "/#/evento?id=" + id;
        String linkCompartilhamento = publicApiUrl + "/api/eventos/" + id + "/compartilhar";
        String metadadosImagem = imagemUrl == null
                ? ""
                : """
                  <meta property="og:image" content="%s">
                  <meta property="og:image:secure_url" content="%s">
                  <meta property="og:image:alt" content="%s">
                  <meta name="twitter:image" content="%s">
                  """.formatted(escapar(imagemUrl), escapar(imagemUrl), escapar(titulo), escapar(imagemUrl));

        String html = """
                <!doctype html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8">
                  <meta property="og:type" content="website">
                  <meta property="og:site_name" content="ChezTicket">
                  <meta property="og:title" content="%s">
                  <meta property="og:description" content="%s">
                  %s
                  <meta property="og:url" content="%s">
                  <meta name="twitter:card" content="summary_large_image">
                  <meta name="twitter:title" content="%s">
                  <meta name="twitter:description" content="%s">
                  <title>%s</title>
                </head>
                <body>
                  <a id="event-link" href="%s">Abrir evento</a>
                  <script>
                    window.location.replace(document.getElementById('event-link').href);
                  </script>
                </body>
                </html>
                """.formatted(
                escapar(titulo),
                escapar(descricao),
                metadadosImagem,
                escapar(linkCompartilhamento),
                escapar(titulo),
                escapar(descricao),
                escapar(titulo),
                escapar(linkEvento));

        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
                .body(html);
    }

    private String urlImagem(String caminho) {
        if (caminho == null || !caminho.startsWith("/api/uploads/")) {
            return null;
        }
        return URI.create(publicApiUrl + "/").resolve(caminho).toString();
    }

    private static String removerBarraFinal(String url) {
        return url.replaceAll("/+$", "");
    }

    private static String escapar(String valor) {
        return HtmlUtils.htmlEscape(valor == null ? "" : valor);
    }
}
