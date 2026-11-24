package com.chezticket.identidade.conta.adapter.out.seguranca;

import com.chezticket.identidade.conta.application.port.out.HashDeSenhaPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class BCryptHashDeSenhaAdapter implements HashDeSenhaPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String senhaEmTexto) {
        return encoder.encode(senhaEmTexto);
    }

    @Override
    public boolean confere(String senhaEmTexto, String senhaHash) {
        return senhaEmTexto != null && senhaHash != null && encoder.matches(senhaEmTexto, senhaHash);
    }
}
