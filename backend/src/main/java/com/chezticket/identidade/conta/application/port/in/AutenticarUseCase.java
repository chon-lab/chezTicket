package com.chezticket.identidade.conta.application.port.in;

import com.chezticket.identidade.conta.domain.Usuario;

public interface AutenticarUseCase {

    /** @throws com.chezticket.identidade.conta.domain.exception.CredenciaisInvalidasException e-mail/senha incorretos */
    Usuario autenticar(String email, String senha);
}
