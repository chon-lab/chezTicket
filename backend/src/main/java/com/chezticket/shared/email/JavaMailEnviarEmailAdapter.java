package com.chezticket.shared.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Envia o e-mail de verdade via SMTP (JavaMailSender, autoconfigurado pelo Spring Boot a partir
 * de {@code spring.mail.*}). Sem {@code chezticket.email.remetente} configurado, só registra um
 * aviso e não envia — nenhuma compra deve falhar por causa do e-mail de confirmação.
 */
@Component
class JavaMailEnviarEmailAdapter implements EnviarEmailPort {

    private static final Logger log = LoggerFactory.getLogger(JavaMailEnviarEmailAdapter.class);

    private final JavaMailSender mailSender;
    private final String remetente;

    JavaMailEnviarEmailAdapter(JavaMailSender mailSender, @Value("${chezticket.email.remetente:}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destinatario, String assunto, String corpoHtml, List<AnexoDeEmail> anexosInline) {
        if (remetente == null || remetente.isBlank()) {
            log.warn("Envio de e-mail não configurado (chezticket.email.remetente ausente); "
                    + "pulando envio para {}.", destinatario);
            return;
        }
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);
            for (AnexoDeEmail anexo : anexosInline) {
                helper.addInline(anexo.contentId(), new ByteArrayResource(anexo.conteudo()), anexo.tipoMime());
            }
            mailSender.send(mensagem);
        } catch (MessagingException | MailException e) {
            log.warn("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }
}
