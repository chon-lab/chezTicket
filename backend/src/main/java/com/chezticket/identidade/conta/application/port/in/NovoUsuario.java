package com.chezticket.identidade.conta.application.port.in;

/** Comando de cadastro. {@code senha} é a senha em texto puro informada pelo usuário. */
public record NovoUsuario(String nome, String email, String senha, String cpf) {
}
