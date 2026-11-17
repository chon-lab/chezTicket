package com.chezticket.identidade;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SessaoUsuario {

    private static final String ATTRIBUTE = "usuarioId";

    private SessaoUsuario() {
    }

    public static Long id(HttpSession session) {
        Object value = session.getAttribute(ATTRIBUTE);
        return value instanceof Long id ? id : null;
    }

    public static long exigirId(HttpSession session) {
        Long id = id(session);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Entre na sua conta para continuar.");
        }
        return id;
    }

    public static void iniciar(HttpSession session, long usuarioId) {
        session.setAttribute(ATTRIBUTE, usuarioId);
    }
}