package com.chezticket.vendas.pedido.adapter.out.email;

import com.chezticket.shared.email.AnexoDeEmail;
import com.chezticket.shared.email.EnviarEmailPort;
import com.chezticket.vendas.pedido.application.port.out.EnviarIngressosPorEmailPort;
import com.chezticket.vendas.pedido.application.port.out.ItemEmailIngresso;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Monta o e-mail de confirmação da compra (um QR Code por ingresso, gerado na hora com ZXing) e
 * delega o envio em si para {@link EnviarEmailPort}. Qualquer falha aqui (geração do QR, SMTP)
 * só vira um aviso no log — nunca deve derrubar a emissão do ingresso em si.
 */
@Component
class EmailDeIngressoAdapter implements EnviarIngressosPorEmailPort {

    private static final Logger log = LoggerFactory.getLogger(EmailDeIngressoAdapter.class);
    private static final int TAMANHO_QR_PX = 240;

    private final EnviarEmailPort enviarEmail;

    EmailDeIngressoAdapter(EnviarEmailPort enviarEmail) {
        this.enviarEmail = enviarEmail;
    }

    @Override
    public void enviar(String destinatario, Long pedidoId, List<ItemEmailIngresso> ingressos) {
        if (destinatario == null || destinatario.isBlank() || ingressos.isEmpty()) {
            return;
        }
        try {
            List<AnexoDeEmail> anexos = new ArrayList<>();
            String corpo = montarCorpoHtml(pedidoId, ingressos, anexos);
            enviarEmail.enviar(destinatario, "Seu ingresso chezTicket — Pedido #" + pedidoId, corpo, anexos);
        } catch (Exception e) {
            log.warn("Falha ao montar o e-mail de confirmação do pedido {}: {}", pedidoId, e.getMessage());
        }
    }

    private static String montarCorpoHtml(Long pedidoId, List<ItemEmailIngresso> ingressos,
            List<AnexoDeEmail> anexos) {
        StringBuilder corpo = new StringBuilder()
                .append("<div style=\"font-family: Arial, sans-serif; color: #1a1a1a;\">")
                .append("<h2>Seu ingresso está confirmado!</h2>")
                .append("<p>Pedido #").append(pedidoId)
                .append(". Apresente o QR Code de cada ingresso na entrada do evento.</p>");

        int indice = 0;
        for (ItemEmailIngresso ingresso : ingressos) {
            String contentId = "qr" + indice++;
            corpo.append("<div style=\"margin:24px 0;padding:16px;border:1px solid #e2e4e7;")
                    .append("border-radius:12px;text-align:center;\">")
                    .append("<p style=\"font-weight:600;margin:0 0 8px;\">")
                    .append(escaparHtml(ingresso.nomeDoTipo()))
                    .append("</p>")
                    .append("<img src=\"cid:").append(contentId).append("\" width=\"").append(TAMANHO_QR_PX)
                    .append("\" height=\"").append(TAMANHO_QR_PX).append("\" alt=\"QR Code do ingresso\" />")
                    .append("<p style=\"font-size:12px;color:#666;margin-top:8px;\">")
                    .append(escaparHtml(ingresso.codigoQr()))
                    .append("</p></div>");
            anexos.add(new AnexoDeEmail(contentId, gerarQrCode(ingresso.codigoQr()), "image/png"));
        }

        corpo.append("<p style=\"color:#666;font-size:13px;\">Este é um e-mail automático do chezTicket.</p>")
                .append("</div>");
        return corpo.toString();
    }

    private static byte[] gerarQrCode(String conteudo) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, TAMANHO_QR_PX,
                    TAMANHO_QR_PX);
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", saida);
            return saida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar o QR Code do ingresso.", e);
        }
    }

    private static String escaparHtml(String valor) {
        return valor == null ? "" : valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
