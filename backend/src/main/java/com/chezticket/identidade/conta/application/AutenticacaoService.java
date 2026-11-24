package com.chezticket.identidade.conta.application;

import com.chezticket.identidade.conta.application.port.in.AutenticarUseCase;
import com.chezticket.identidade.conta.application.port.in.CadastrarUsuarioUseCase;
import com.chezticket.identidade.conta.application.port.in.ConsultarUsuarioUseCase;
import com.chezticket.identidade.conta.application.port.in.NovoUsuario;
import com.chezticket.identidade.conta.application.port.out.HashDeSenhaPort;
import com.chezticket.identidade.conta.application.port.out.UsuarioRepositorio;
import com.chezticket.identidade.conta.domain.Usuario;
import com.chezticket.identidade.conta.domain.exception.CpfJaCadastradoException;
import com.chezticket.identidade.conta.domain.exception.CredenciaisInvalidasException;
import com.chezticket.identidade.conta.domain.exception.EmailJaCadastradoException;
import com.chezticket.shared.domain.NaoAutenticadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AutenticacaoService implements CadastrarUsuarioUseCase, AutenticarUseCase, ConsultarUsuarioUseCase {

    private final UsuarioRepositorio repositorio;
    private final HashDeSenhaPort hashDeSenha;

    public AutenticacaoService(UsuarioRepositorio repositorio, HashDeSenhaPort hashDeSenha) {
        this.repositorio = repositorio;
        this.hashDeSenha = hashDeSenha;
    }

    @Override
    public Usuario cadastrar(NovoUsuario comando) {
        Usuario candidato = Usuario.cadastrar(comando.nome(), comando.email(), "temporario", comando.cpf());
        if (repositorio.existePorEmail(candidato.email())) {
            throw new EmailJaCadastradoException(candidato.email());
        }
        if (repositorio.existePorCpf(candidato.cpf())) {
            throw new CpfJaCadastradoException(candidato.cpf());
        }
        Usuario usuario = Usuario.cadastrar(comando.nome(), comando.email(), hashDeSenha.hash(comando.senha()),
                comando.cpf());
        // Modelo simplificado: toda conta também vira organizador, sem coletar razão social e
        // documento próprios (o formulário de cadastro só pede nome/e-mail/senha/CPF).
        return repositorio.cadastrar(usuario, usuario.nome(), usuario.cpf());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        String emailNormalizado = email == null ? "" : email.strip().toLowerCase();
        Usuario usuario = repositorio.buscarPorEmail(emailNormalizado)
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!hashDeSenha.confere(senha, usuario.senhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NaoAutenticadoException("Sessão inválida ou expirada."));
    }
}
