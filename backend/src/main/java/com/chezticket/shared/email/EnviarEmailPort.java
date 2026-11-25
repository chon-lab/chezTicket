package com.chezticket.shared.email;

import java.util.List;

/**
 * Porta de saída: envia um e-mail HTML, com anexos inline opcionais (ex.: imagens de QR Code
 * referenciadas no corpo via {@code cid:}). Falha de envio nunca deve propagar para quem chama —
 * é um efeito colateral best-effort, não uma etapa obrigatória da compra (ver
 * {@code JavaMailEnviarEmailAdapter}).
 */
public interface EnviarEmailPort {

    void enviar(String destinatario, String assunto, String corpoHtml, List<AnexoDeEmail> anexosInline);
}
