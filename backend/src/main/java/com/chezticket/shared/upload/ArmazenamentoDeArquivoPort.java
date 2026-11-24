package com.chezticket.shared.upload;

import org.springframework.web.multipart.MultipartFile;

/** Porta de saída: persiste um arquivo enviado e devolve o caminho relativo para servi-lo. */
public interface ArmazenamentoDeArquivoPort {

    /**
     * Salva o arquivo em disco com um nome único e devolve o caminho relativo
     * (ex.: {@code /uploads/3f9c2e-capa.jpg}), pronto para ser resolvido por
     * {@link com.chezticket.shared.web.UploadUrlResolver}.
     */
    String salvar(MultipartFile arquivo);
}
