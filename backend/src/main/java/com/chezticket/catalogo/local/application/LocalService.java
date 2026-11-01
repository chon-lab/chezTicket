package com.chezticket.catalogo.local.application;

import com.chezticket.catalogo.local.application.port.in.AtualizarLocalUseCase;
import com.chezticket.catalogo.local.application.port.in.CadastrarLocalUseCase;
import com.chezticket.catalogo.local.application.port.in.ConsultarLocaisUseCase;
import com.chezticket.catalogo.local.application.port.in.DadosLocal;
import com.chezticket.catalogo.local.application.port.in.NovoLocal;
import com.chezticket.catalogo.local.application.port.in.RemoverLocalUseCase;
import com.chezticket.catalogo.local.application.port.out.LocalRepositorio;
import com.chezticket.catalogo.local.domain.Local;
import com.chezticket.catalogo.local.domain.exception.LocalNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocalService implements
        CadastrarLocalUseCase,
        ConsultarLocaisUseCase,
        AtualizarLocalUseCase,
        RemoverLocalUseCase {

    private final LocalRepositorio repositorio;

    public LocalService(LocalRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Local cadastrar(NovoLocal comando) {
        Local local = Local.novo(comando.nome(), comando.endereco(), comando.cidade(), comando.uf(),
                comando.capacidadeTotal());
        return repositorio.salvar(local);
    }

    @Override
    @Transactional(readOnly = true)
    public Local buscarPorId(Long id) {
        return carregar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Local> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public Local atualizar(Long id, DadosLocal dados) {
        Local local = carregar(id);
        local.atualizarDados(dados.nome(), dados.endereco(), dados.cidade(), dados.uf(), dados.capacidadeTotal());
        return repositorio.salvar(local);
    }

    @Override
    public void remover(Long id) {
        carregar(id);
        repositorio.remover(id);
    }

    private Local carregar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new LocalNaoEncontradoException(id));
    }
}
