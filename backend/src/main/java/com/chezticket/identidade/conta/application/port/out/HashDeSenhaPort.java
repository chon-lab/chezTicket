package com.chezticket.identidade.conta.application.port.out;

/** Porta de saída: hash e verificação de senha. Implementada com BCrypt. */
public interface HashDeSenhaPort {

    String hash(String senhaEmTexto);

    boolean confere(String senhaEmTexto, String senhaHash);
}
