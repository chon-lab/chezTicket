package com.chezticket.catalogo.local.adapter.in.web.dto;

import com.chezticket.catalogo.local.domain.Local;
import java.time.LocalDateTime;

public record LocalResponse(
        Long id,
        String nome,
        String endereco,
        String cidade,
        String uf,
        int capacidadeTotal,
        LocalDateTime criadoEm) {

    public static LocalResponse de(Local local) {
        return new LocalResponse(
                local.id(),
                local.nome(),
                local.endereco(),
                local.cidade(),
                local.uf(),
                local.capacidadeTotal(),
                local.criadoEm());
    }
}
