package com.chezticket.catalogo.categoria.adapter.out.persistence;

import com.chezticket.catalogo.categoria.application.port.out.CategoriaRepositorio;
import com.chezticket.catalogo.categoria.domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de saída: implementa a porta {@link CategoriaRepositorio} com JPA + MariaDB. */
@Component
class CategoriaPersistenceAdapter implements CategoriaRepositorio {

    private final CategoriaJpaRepository jpa;

    CategoriaPersistenceAdapter(CategoriaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        CategoriaJpaEntity salva = jpa.save(CategoriaPersistenceMapper.paraEntidade(categoria));
        return CategoriaPersistenceMapper.paraDominio(salva);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return jpa.findById(id).map(CategoriaPersistenceMapper::paraDominio);
    }

    @Override
    public boolean existePorId(Long id) {
        return id != null && jpa.existsById(id);
    }

    @Override
    public List<Categoria> listarTodas() {
        return jpa.findAllByOrderByNomeAsc().stream()
                .map(CategoriaPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Categoria> listarAtivas() {
        return jpa.findAllByAtivaTrueOrderByNomeAsc().stream()
                .map(CategoriaPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpa.existsByNome(nome);
    }

    @Override
    public boolean existePorNomeExcetoId(String nome, Long id) {
        return jpa.existsByNomeAndIdNot(nome, id);
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }
}
