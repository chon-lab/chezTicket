package com.chezticket.vendas.pedido.adapter.in.web.dto;

import com.chezticket.vendas.pagamento.domain.MotivoReembolso;
import com.chezticket.vendas.pagamento.domain.TipoReembolso;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @param valor obrigatório só quando {@code tipo} é {@link TipoReembolso#PARCIAL}; para
 *              {@link TipoReembolso#TOTAL} é ignorado (o valor vem do pagamento registrado)
 */
public record SolicitarReembolsoRequest(
        @NotNull
        MotivoReembolso motivo,
        @NotNull
        TipoReembolso tipo,
        BigDecimal valor) {
}
