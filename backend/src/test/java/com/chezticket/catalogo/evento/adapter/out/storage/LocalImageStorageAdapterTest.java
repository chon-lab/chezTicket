package com.chezticket.catalogo.evento.adapter.out.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;
import com.chezticket.shared.domain.RegraDeNegocioException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalImageStorageAdapterTest {

    @TempDir
    Path tempDirectory;

    @Test
    void armazenaImagemComNomeUnicoEPermiteRemocao() throws IOException {
        LocalImageStorageAdapter adapter = new LocalImageStorageAdapter(tempDirectory.toString());
        byte[] pngSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        String url = adapter.armazenar(new ImagemUpload(pngSignature, "image/png"));
        Path storedFile = tempDirectory.resolve(url.substring("/api/uploads/".length()));

        assertThat(url).startsWith("/api/uploads/").endsWith(".png");
        assertThat(Files.readAllBytes(storedFile)).containsExactly(pngSignature);

        adapter.excluir(url);

        assertThat(Files.exists(storedFile)).isFalse();
    }

    @Test
    void rejeitaArquivoComContentTypeOuAssinaturaIncompativeis() {
        LocalImageStorageAdapter adapter = new LocalImageStorageAdapter(tempDirectory.toString());

        assertThatThrownBy(() -> adapter.armazenar(
                new ImagemUpload(new byte[] {1, 2, 3}, "image/png")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void rejeitaImagemMaiorQueCincoMegabytes() {
        LocalImageStorageAdapter adapter = new LocalImageStorageAdapter(tempDirectory.toString());
        byte[] tooLarge = new byte[5 * 1024 * 1024 + 1];

        assertThatThrownBy(() -> adapter.armazenar(new ImagemUpload(tooLarge, "image/png")))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("5 MB");
    }
}
