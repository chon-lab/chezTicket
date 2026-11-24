package com.chezticket.vendas.pagamento.application.port.out;

import com.chezticket.vendas.pagamento.domain.Pagamento;
import java.util.Optional;

public interface PagamentoRepositorio {

    Pagamento salvar(Pagamento pagamento);

    Optional<Pagamento> buscarPorId(Long id);

    Optional<Pagamento> buscarPorPedidoId(Long pedidoId);
}
