package com.chezticket.catalogo.evento.domain;

/** Política de reembolso apresentada e aceita antes do pagamento (RN-05). */
public enum PoliticaReembolso {
    FLEXIVEL("Reembolso integral até 24h antes do evento"),
    PADRAO("Reembolso conforme legislação de consumo e antecedência do evento"),
    SEM_REEMBOLSO("Sem reembolso, exceto cancelamento ou remarcação pelo organizador");

    private final String descricao;

    PoliticaReembolso(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}
