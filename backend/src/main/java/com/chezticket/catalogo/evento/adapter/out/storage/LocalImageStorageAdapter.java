package com.chezticket.catalogo.evento.adapter.out.storage;

import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;
import com.chezticket.catalogo.evento.application.port.out.ArmazenarImagemPort;
import com.chezticket.shared.domain.RegraDeNegocioException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalImageStorageAdapter implements ArmazenarImagemPort {

    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final Path directory;

    public LocalImageStorageAdapter(
            @Value("${chezticket.storage.upload-dir:../uploads}") String uploadDirectory) {
        this.directory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public String armazenar(ImagemUpload imagem) {
        if (imagem.conteudo() == null || imagem.conteudo().length == 0) {
            throw new RegraDeNegocioException("O arquivo de imagem está vazio.");
        }
        if (imagem.conteudo().length > MAX_FILE_SIZE) {
            throw new RegraDeNegocioException("Cada imagem deve ter no máximo 5 MB.");
        }

        String extension = extensionFor(imagem.contentType(), imagem.conteudo());
        String filename = UUID.randomUUID() + extension;
        Path target = directory.resolve(filename).normalize();
        if (!target.startsWith(directory)) {
            throw new RegraDeNegocioException("Nome de arquivo inválido.");
        }

        try {
            Files.createDirectories(directory);
            Files.write(target, imagem.conteudo(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return "/api/uploads/" + filename;
        } catch (IOException error) {
            throw new UncheckedIOException("Não foi possível salvar a imagem enviada.", error);
        }
    }

    @Override
    public void excluir(String url) {
        if (url == null) {
            return;
        }
        String prefix = "/api/uploads/";
        if (!url.startsWith(prefix)) {
            throw new IllegalArgumentException("URL de imagem fora do armazenamento local.");
        }
        String filename = url.substring(prefix.length());
        Path target = directory.resolve(filename).normalize();
        if (!target.startsWith(directory) || filename.contains("/")) {
            throw new IllegalArgumentException("Caminho de imagem inválido.");
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException error) {
            throw new UncheckedIOException("Não foi possível remover a imagem temporária.", error);
        }
    }

    public String resourceLocation() {
        String uri = directory.toUri().toString();
        return uri.endsWith("/") ? uri : uri + "/";
    }

    private String extensionFor(String contentType, byte[] bytes) {
        if ("image/jpeg".equals(contentType)
                && bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }
        if ("image/png".equals(contentType)
                && bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A) {
            return ".png";
        }
        if ("image/webp".equals(contentType)
                && bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P') {
            return ".webp";
        }
        throw new RegraDeNegocioException("Envie uma imagem JPG, PNG ou WEBP válida.");
    }
}
