package com.chezticket.vendas.pagamento.application.port.out;

import com.chezticket.vendas.pagamento.domain.Reembolso;
import java.util.List;
import java.util.Optional;

public interface ReembolsoRepositorio {

    Reembolso salvar(Reembolso reembolso);

    Optional<Reembolso> buscarPorId(Long id);

    List<Reembolso> listarPorPagamento(Long pagamentoId);
}
