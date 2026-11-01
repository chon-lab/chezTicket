package com.chezticket.catalogo.ingresso.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

/**
 * {@code @Version} dá controle de concorrência otimista: duas reservas simultâneas no mesmo
 * tipo de ingresso não derrubam uma a atualização da outra silenciosamente — a segunda a
 * tentar salvar recebe {@code OptimisticLockingFailureException} (mapeada para HTTP 409).
 * É a garantia de fato por trás do "controle de estoque e disponibilidade" do cronograma.
 */
@Entity
@Table(name = "tipo_ingresso")
class TipoIngressoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sessao_id", nullable = false)
    private Long sessaoId;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private boolean gratuito;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "taxa_servico", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxaServico;

    @Column(name = "quantidade_total", nullable = false)
    private int quantidadeTotal;

    @Column(name = "quantidade_vendida", nullable = false)
    private int quantidadeVendida;

    @Column(name = "limite_por_compra", nullable = false)
    private int limitePorCompra;

    @Version
    @Column(nullable = false)
    private long versao;

    protected TipoIngressoJpaEntity() {
    }

    TipoIngressoJpaEntity(Long id, Long sessaoId, String nome, boolean gratuito, BigDecimal preco,
            BigDecimal taxaServico, int quantidadeTotal, int quantidadeVendida, int limitePorCompra,
            long versao) {
        this.id = id;
        this.sessaoId = sessaoId;
        this.nome = nome;
        this.gratuito = gratuito;
        this.preco = preco;
        this.taxaServico = taxaServico;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeVendida = quantidadeVendida;
        this.limitePorCompra = limitePorCompra;
        this.versao = versao;
    }

    Long getId() {
        return id;
    }

    Long getSessaoId() {
        return sessaoId;
    }

    String getNome() {
        return nome;
    }

    boolean isGratuito() {
        return gratuito;
    }

    BigDecimal getPreco() {
        return preco;
    }

    BigDecimal getTaxaServico() {
        return taxaServico;
    }

    int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    int getLimitePorCompra() {
        return limitePorCompra;
    }

    long getVersao() {
        return versao;
    }
}
