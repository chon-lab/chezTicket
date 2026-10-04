package com.chezticket.catalogo.evento.domain;

/** Classificação indicativa exibida ao comprador antes da conclusão da compra (RN-04). */
public enum ClassificacaoEtaria {
    LIVRE("Livre"),
    DEZ("10 anos"),
    DOZE("12 anos"),
    QUATORZE("14 anos"),
    DEZESSEIS("16 anos"),
    DEZOITO("18 anos");

    private final String descricao;

    ClassificacaoEtaria(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}
