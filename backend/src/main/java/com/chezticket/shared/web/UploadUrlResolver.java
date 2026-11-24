package com.chezticket.shared.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Resolve o caminho relativo salvo no banco (ex.: {@code /uploads/abc123.jpg}) para a URL que o
 * front deve usar. Em dev, {@code chezticket.public-api-url} fica vazio e a URL é relativa — passa
 * pelo proxy do Vite. Em produção, com front e back em origens diferentes, a propriedade dá a
 * origem pública da API e a URL devolvida já vem absoluta.
 */
@Component
public class UploadUrlResolver {

    private final String publicApiUrl;

    public UploadUrlResolver(@Value("${chezticket.public-api-url:}") String publicApiUrl) {
        this.publicApiUrl = publicApiUrl == null ? "" : publicApiUrl.strip().replaceAll("/+$", "");
    }

    /**
     * Resolve o valor salvo. Um caminho relativo de upload (ex.: {@code /uploads/abc.jpg}) vira
     * a URL servida pela API; uma URL já absoluta (http(s)://, usada por quem cola um link de
     * imagem externa pelo endpoint simples) é devolvida sem alteração.
     */
    public String resolver(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        if (valor.startsWith("http://") || valor.startsWith("https://")) {
            return valor;
        }
        return publicApiUrl + "/api" + valor;
    }
}
