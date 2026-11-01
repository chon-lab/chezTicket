package com.chezticket.catalogo.local.application.port.in;

/** Comando para cadastrar um local novo. */
public record NovoLocal(
        String nome,
        String endereco,
        String cidade,
        String uf,
        int capacidadeTotal) {
}
