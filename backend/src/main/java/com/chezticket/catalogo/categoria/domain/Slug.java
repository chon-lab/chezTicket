package com.chezticket.catalogo.categoria.domain;

import java.text.Normalizer;
import java.util.Locale;

/** Converte um texto livre em um slug para URL ("Cultura e Arte" &rarr; "cultura-e-arte"). */
public final class Slug {

    private Slug() {
    }

    public static String de(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
