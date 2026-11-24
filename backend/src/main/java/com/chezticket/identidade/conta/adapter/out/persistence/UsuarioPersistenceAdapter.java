package com.chezticket.identidade.conta.adapter.out.persistence;

import com.chezticket.identidade.conta.application.port.out.UsuarioRepositorio;
import com.chezticket.identidade.conta.domain.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class UsuarioPersistenceAdapter implements UsuarioRepositorio {

    private final UsuarioJpaRepository usuarios;
    private final ParticipanteJpaRepository participantes;
    private final OrganizadorJpaRepository organizadores;

    UsuarioPersistenceAdapter(UsuarioJpaRepository usuarios, ParticipanteJpaRepository participantes,
            OrganizadorJpaRepository organizadores) {
        this.usuarios = usuarios;
        this.participantes = participantes;
        this.organizadores = organizadores;
    }

    @Override
    public Usuario cadastrar(Usuario usuario, String razaoSocialOrganizador, String documentoOrganizador) {
        UsuarioJpaEntity salvo = usuarios.save(new UsuarioJpaEntity(
                null, usuario.nome(), usuario.email(), usuario.senhaHash(), usuario.status(), usuario.criadoEm()));
        participantes.save(new ParticipanteJpaEntity(salvo.getId(), usuario.cpf()));
        organizadores.save(new OrganizadorJpaEntity(salvo.getId(), razaoSocialOrganizador, documentoOrganizador));
        return paraDominio(salvo, usuario.cpf());
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarios.findById(id).map(entidade -> paraDominio(entidade, cpfDe(entidade.getId())));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.findByEmail(email).map(entidade -> paraDominio(entidade, cpfDe(entidade.getId())));
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarios.existsByEmail(email);
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return participantes.existsByCpf(cpf);
    }

    private String cpfDe(Long usuarioId) {
        return participantes.findById(usuarioId).map(ParticipanteJpaEntity::getCpf).orElse(null);
    }

    private static Usuario paraDominio(UsuarioJpaEntity entidade, String cpf) {
        return Usuario.reconstituir(entidade.getId(), entidade.getNome(), entidade.getEmail(),
                entidade.getSenhaHash(), cpf, entidade.getStatus(), entidade.getCriadoEm());
    }
}
