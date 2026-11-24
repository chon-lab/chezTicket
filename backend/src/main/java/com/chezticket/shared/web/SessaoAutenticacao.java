package com.chezticket.shared.web;

import com.chezticket.shared.domain.NaoAutenticadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Autenticação simples baseada na sessão do servlet (cookie {@code JSESSIONID}), sem JWT.
 * O id do usuário autenticado fica num atributo de sessão; não há nada além disso — é
 * deliberadamente simples, documentado como tal no plano de integração com o front.
 */
public final class SessaoAutenticacao {

    private static final String ATRIBUTO_USUARIO_ID = "usuarioId";

    private SessaoAutenticacao() {
    }

    public static void autenticar(HttpServletRequest request, Long usuarioId) {
        request.getSession(true).setAttribute(ATRIBUTO_USUARIO_ID, usuarioId);
    }

    public static Optional<Long> usuarioIdOpcional(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((Long) sessao.getAttribute(ATRIBUTO_USUARIO_ID));
    }

    public static Long usuarioIdObrigatorio(HttpServletRequest request) {
        return usuarioIdOpcional(request)
                .orElseThrow(() -> new NaoAutenticadoException("Entre na sua conta para continuar."));
    }

    public static void encerrar(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }
}
