package com.chezticket.shared.email;

/** Um anexo inline (ex.: imagem de QR Code referenciada no HTML via {@code cid:contentId}). */
public record AnexoDeEmail(String contentId, byte[] conteudo, String tipoMime) {
}
