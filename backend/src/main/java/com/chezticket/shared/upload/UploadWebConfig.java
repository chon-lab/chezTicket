package com.chezticket.shared.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serve os arquivos salvos por {@link ArmazenamentoLocalDeArquivos} em {@code /api/uploads/**}. */
@Configuration
class UploadWebConfig implements WebMvcConfigurer {

    private final String diretorio;

    UploadWebConfig(@Value("${chezticket.uploads-dir}") String diretorio) {
        this.diretorio = diretorio;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + comBarraFinal(diretorio));
    }

    private static String comBarraFinal(String diretorio) {
        String normalizado = diretorio.replace('\\', '/');
        return normalizado.endsWith("/") ? normalizado : normalizado + "/";
    }
}
