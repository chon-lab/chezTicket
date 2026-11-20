package com.chezticket.catalogo.evento.adapter.out.storage;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class UploadWebConfiguration implements WebMvcConfigurer {

    private final LocalImageStorageAdapter storage;

    UploadWebConfiguration(LocalImageStorageAdapter storage) {
        this.storage = storage;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(storage.resourceLocation());
    }
}
