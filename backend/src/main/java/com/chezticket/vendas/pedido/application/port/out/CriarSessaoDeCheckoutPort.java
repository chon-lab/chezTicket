package com.chezticket.vendas.pedido.application.port.out;

/** Porta de saída: abstrai o gateway de pagamento (Stripe Checkout) na criação da sessão de compra. */
public interface CriarSessaoDeCheckoutPort {

    /** @return a URL para onde o comprador deve ser redirecionado para pagar. */
    String criar(SolicitacaoDeCheckout solicitacao);
}
