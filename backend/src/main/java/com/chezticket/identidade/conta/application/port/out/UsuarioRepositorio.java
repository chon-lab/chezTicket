package com.chezticket.identidade.conta.application.port.out;

import com.chezticket.identidade.conta.domain.Usuario;
import java.util.Optional;

/**
 * Porta de saída: persistência da conta de usuário.
 *
 * <p>{@code cadastrar} grava também as linhas de {@code participante} e {@code organizador}
 * associadas (ver {@link Usuario}) — é uma única operação porque, neste modelo simplificado,
 * as três sempre nascem juntas.
 */
public interface UsuarioRepositorio {

    Usuario cadastrar(Usuario usuario, String razaoSocialOrganizador, String documentoOrganizador);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);

    boolean existePorCpf(String cpf);
}
