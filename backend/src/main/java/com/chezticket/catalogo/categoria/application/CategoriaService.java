package com.chezticket.catalogo.categoria.application;

import com.chezticket.catalogo.categoria.application.port.in.AtualizarCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.CadastrarCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.ConsultarCategoriasUseCase;
import com.chezticket.catalogo.categoria.application.port.in.GerenciarStatusDaCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.in.RemoverCategoriaUseCase;
import com.chezticket.catalogo.categoria.application.port.out.CategoriaRepositorio;
import com.chezticket.catalogo.categoria.domain.Categoria;
import com.chezticket.catalogo.categoria.domain.exception.CategoriaJaExisteException;
import com.chezticket.catalogo.categoria.domain.exception.CategoriaNaoEncontradaException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação: implementa os casos de uso da categoria orquestrando
 * o domínio e a porta de saída {@link CategoriaRepositorio}.
 *
 * <p>As anotações do Spring aqui são um compromisso pragmático de time — o pacote
 * {@code domain} permanece 100% livre de framework, que é o que a arquitetura
 * hexagonal exige de fato proteger.
 */
@Service
@Transactional
public class CategoriaService implements
        CadastrarCategoriaUseCase,
        ConsultarCategoriasUseCase,
        AtualizarCategoriaUseCase,
        GerenciarStatusDaCategoriaUseCase,
        RemoverCategoriaUseCase {

    private final CategoriaRepositorio repositorio;

    public CategoriaService(CategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Categoria cadastrar(String nome) {
        Categoria categoria = Categoria.nova(nome);
        if (repositorio.existePorNome(categoria.nome())) {
            throw new CategoriaJaExisteException(categoria.nome());
        }
        return repositorio.salvar(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listar(boolean apenasAtivas) {
        return apenasAtivas ? repositorio.listarAtivas() : repositorio.listarTodas();
    }

    @Override
    public Categoria atualizar(Long id, String novoNome) {
        Categoria categoria = carregar(id);
        categoria.renomear(novoNome);
        if (repositorio.existePorNomeExcetoId(categoria.nome(), id)) {
            throw new CategoriaJaExisteException(categoria.nome());
        }
        return repositorio.salvar(categoria);
    }

    @Override
    public Categoria ativar(Long id) {
        Categoria categoria = carregar(id);
        categoria.ativar();
        return repositorio.salvar(categoria);
    }

    @Override
    public Categoria desativar(Long id) {
        Categoria categoria = carregar(id);
        categoria.desativar();
        return repositorio.salvar(categoria);
    }

    @Override
    public void remover(Long id) {
        carregar(id);
        repositorio.remover(id);
    }

    private Categoria carregar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }
}
