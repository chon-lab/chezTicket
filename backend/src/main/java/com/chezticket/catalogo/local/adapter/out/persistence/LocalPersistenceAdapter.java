package com.chezticket.catalogo.local.adapter.out.persistence;

import com.chezticket.catalogo.local.application.port.out.LocalRepositorio;
import com.chezticket.catalogo.local.domain.Local;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class LocalPersistenceAdapter implements LocalRepositorio {

    private final LocalJpaRepository jpa;

    LocalPersistenceAdapter(LocalJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Local salvar(Local local) {
        return LocalPersistenceMapper.paraDominio(
                jpa.save(LocalPersistenceMapper.paraEntidade(local)));
    }

    @Override
    public Optional<Local> buscarPorId(Long id) {
        return jpa.findById(id).map(LocalPersistenceMapper::paraDominio);
    }

    @Override
    public boolean existePorId(Long id) {
        return id != null && jpa.existsById(id);
    }

    @Override
    public List<Local> listarTodos() {
        return jpa.findAllByOrderByNomeAsc().stream()
                .map(LocalPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }
}
