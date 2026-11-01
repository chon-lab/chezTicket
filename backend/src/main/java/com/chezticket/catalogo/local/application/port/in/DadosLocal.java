package com.chezticket.catalogo.local.application.port.in;

/** Comando para atualizar os dados de um local existente. */
public record DadosLocal(
        String nome,
        String endereco,
        String cidade,
        String uf,
        int capacidadeTotal) {
}
