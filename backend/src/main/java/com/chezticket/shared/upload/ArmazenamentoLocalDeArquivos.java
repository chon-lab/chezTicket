package com.chezticket.shared.upload;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Salva arquivos enviados no disco local, num diretório montado como volume no Docker
 * (ver {@code docker-compose.yml}) para sobreviver a reinícios do container. Servidos
 * estaticamente em {@code /api/uploads/**} (ver {@link UploadWebConfig}).
 */
@Component
class ArmazenamentoLocalDeArquivos implements ArmazenamentoDeArquivoPort {

    private final Path diretorio;

    ArmazenamentoLocalDeArquivos(@Value("${chezticket.uploads-dir}") String diretorioConfigurado) {
        this.diretorio = Path.of(diretorioConfigurado).toAbsolutePath().normalize();
        try {
            Files.createDirectories(diretorio);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar o diretório de uploads: " + diretorio, e);
        }
    }

    @Override
    public String salvar(MultipartFile arquivo) {
        String extensao = extensaoDe(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = diretorio.resolve(nomeArquivo);
        try {
            arquivo.transferTo(destino);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível salvar o arquivo enviado.", e);
        }
        return "/uploads/" + nomeArquivo;
    }

    /**
     * Extrai só a extensão (ex.: {@code .jpg}), validando contra uma lista estrita de
     * caracteres — o nome original do arquivo vem do cliente e não é confiável; sem essa
     * validação, uma extensão forjada com {@code /} ou {@code ..} escaparia do diretório de
     * destino ao ser usada em {@link Path#resolve}.
     */
    private static String extensaoDe(String nomeOriginal) {
        if (nomeOriginal == null) {
            return "";
        }
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto < 0) {
            return "";
        }
        String extensao = nomeOriginal.substring(ponto + 1).toLowerCase();
        return extensao.matches("[a-z0-9]{1,5}") ? "." + extensao : "";
    }
}
