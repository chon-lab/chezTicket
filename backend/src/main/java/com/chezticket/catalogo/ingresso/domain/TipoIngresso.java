package com.chezticket.catalogo.ingresso.domain;

import com.chezticket.catalogo.ingresso.domain.exception.EstoqueInsuficienteException;
import com.chezticket.catalogo.ingresso.domain.exception.TipoIngressoInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Tipo de ingresso vendido em uma sessão (ex.: "Inteira", "Meia", "VIP"). Modelo de domínio puro.
 *
 * <p>Concentra a regra de formação (gratuito implica preço/taxa zero) e o controle de estoque
 * (quantidade vendida nunca pode superar a quantidade total — RN de disponibilidade).
 */
public class TipoIngresso {

    private static final int NOME_MIN = 2;
    private static final int NOME_MAX = 80;
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

    private Long id;
    private final Long sessaoId;
    private String nome;
    private boolean gratuito;
    private BigDecimal preco;
    private BigDecimal taxaServico;
    private int quantidadeTotal;
    private int quantidadeVendida;
    private int limitePorCompra;
    private LocalDate inicioVendas;
    private LocalDate fimVendas;
    private final long versao;

    private TipoIngresso(Long id, Long sessaoId, String nome, boolean gratuito, BigDecimal preco,
            BigDecimal taxaServico, int quantidadeTotal, int quantidadeVendida, int limitePorCompra,
            LocalDate inicioVendas, LocalDate fimVendas, long versao) {
        this.id = id;
        this.sessaoId = sessaoId;
        this.nome = nome;
        this.gratuito = gratuito;
        this.preco = preco;
        this.taxaServico = taxaServico;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeVendida = quantidadeVendida;
        this.limitePorCompra = limitePorCompra;
        this.inicioVendas = inicioVendas;
        this.fimVendas = fimVendas;
        this.versao = versao;
    }

    /** Cria um tipo de ingresso novo, sem nenhuma unidade vendida ainda. */
    public static TipoIngresso criar(Long sessaoId, String nome, boolean gratuito, BigDecimal preco,
            BigDecimal taxaServico, int quantidadeTotal, int limitePorCompra) {
        return criar(sessaoId, nome, gratuito, preco, taxaServico, quantidadeTotal, limitePorCompra, null, null);
    }

    public static TipoIngresso criar(Long sessaoId, String nome, boolean gratuito, BigDecimal preco,
            BigDecimal taxaServico, int quantidadeTotal, int limitePorCompra,
            LocalDate inicioVendas, LocalDate fimVendas) {
        exigir(sessaoId != null, "A sessão do tipo de ingresso é obrigatória.");
        validarQuantidadeTotal(quantidadeTotal);
        validarPeriodoVendas(inicioVendas, fimVendas);
        return new TipoIngresso(
                null,
                sessaoId,
                validarNome(nome),
                gratuito,
                precoEfetivo(gratuito, preco),
                taxaEfetiva(gratuito, taxaServico),
                quantidadeTotal,
                0,
                validarLimitePorCompra(limitePorCompra, quantidadeTotal),
                inicioVendas,
                fimVendas,
                0);
    }

    /**
     * Reconstrói um tipo de ingresso já existente. Usado pelo adaptador de persistência.
     *
     * <p>{@code versao} é o valor de bloqueio otimista lido do banco (coluna {@code versao},
     * JPA {@code @Version}) — carregá-lo de volta a cada leitura é o que garante que um
     * {@code salvar()} posterior detecte uma escrita concorrente em vez de sempre comparar
     * contra zero.</p>
     */
    public static TipoIngresso reconstituir(Long id, Long sessaoId, String nome, boolean gratuito,
            BigDecimal preco, BigDecimal taxaServico, int quantidadeTotal, int quantidadeVendida,
            int limitePorCompra, long versao) {
        return reconstituir(id, sessaoId, nome, gratuito, preco, taxaServico, quantidadeTotal,
                quantidadeVendida, limitePorCompra, null, null, versao);
    }

    public static TipoIngresso reconstituir(Long id, Long sessaoId, String nome, boolean gratuito,
            BigDecimal preco, BigDecimal taxaServico, int quantidadeTotal, int quantidadeVendida,
            int limitePorCompra, LocalDate inicioVendas, LocalDate fimVendas, long versao) {
        return new TipoIngresso(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(sessaoId, "sessaoId"),
                nome,
                gratuito,
                preco,
                taxaServico,
                quantidadeTotal,
                quantidadeVendida,
                limitePorCompra,
                inicioVendas,
                fimVendas,
                versao);
    }

    public void atualizarDados(String nome, boolean gratuito, BigDecimal preco, BigDecimal taxaServico,
            int quantidadeTotal, int limitePorCompra) {
        exigir(quantidadeTotal >= quantidadeVendida,
                "A nova quantidade total (" + quantidadeTotal + ") não pode ser menor que a quantidade já "
                        + "vendida/reservada (" + quantidadeVendida + ").");
        this.nome = validarNome(nome);
        this.gratuito = gratuito;
        this.preco = precoEfetivo(gratuito, preco);
        this.taxaServico = taxaEfetiva(gratuito, taxaServico);
        this.quantidadeTotal = quantidadeTotal;
        this.limitePorCompra = validarLimitePorCompra(limitePorCompra, quantidadeTotal);
    }

    /** Quantas unidades ainda podem ser vendidas/reservadas. */
    public int quantidadeDisponivel() {
        return quantidadeTotal - quantidadeVendida;
    }

    /** Figura 2 da modelagem: {@code +boolean temDisponibilidade(int qtd)}. */
    public boolean temDisponibilidade(int quantidade) {
        return quantidade > 0 && quantidade <= quantidadeDisponivel();
    }

    /** Reserva/vende {@code quantidade} unidades, respeitando o limite por compra e o estoque. */
    public void reservarUnidades(int quantidade) {
        exigir(quantidade > 0, "A quantidade deve ser maior que zero.");
        if (quantidade > limitePorCompra) {
            throw new TipoIngressoInvalidoException(
                    "Quantidade " + quantidade + " excede o limite de " + limitePorCompra + " por compra.");
        }
        if (!temDisponibilidade(quantidade)) {
            throw new EstoqueInsuficienteException(id, quantidadeDisponivel(), quantidade);
        }
        this.quantidadeVendida += quantidade;
    }

    /** Libera {@code quantidade} unidades de volta ao estoque (cancelamento/expiração de reserva). */
    public void liberarUnidades(int quantidade) {
        exigir(quantidade > 0, "A quantidade deve ser maior que zero.");
        this.quantidadeVendida = Math.max(0, this.quantidadeVendida - quantidade);
    }

    public boolean podeSerRemovido() {
        return quantidadeVendida == 0;
    }

    private static String validarNome(String nome) {
        String limpo = nome == null ? "" : nome.strip().replaceAll("\\s{2,}", " ");
        if (limpo.length() < NOME_MIN || limpo.length() > NOME_MAX) {
            throw new TipoIngressoInvalidoException(
                    "O nome do tipo de ingresso deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        return limpo;
    }

    private static void validarQuantidadeTotal(int quantidadeTotal) {
        if (quantidadeTotal <= 0) {
            throw new TipoIngressoInvalidoException("A quantidade total deve ser maior que zero.");
        }
    }

    private static void validarPeriodoVendas(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new TipoIngressoInvalidoException(
                    "O encerramento das vendas deve ser na mesma data ou depois do início.");
        }
    }

    private static int validarLimitePorCompra(int limitePorCompra, int quantidadeTotal) {
        if (limitePorCompra <= 0) {
            throw new TipoIngressoInvalidoException("O limite por compra deve ser maior que zero.");
        }
        if (limitePorCompra > quantidadeTotal) {
            throw new TipoIngressoInvalidoException("O limite por compra não pode exceder a quantidade total.");
        }
        return limitePorCompra;
    }

    private static BigDecimal precoEfetivo(boolean gratuito, BigDecimal preco) {
        if (gratuito) {
            return ZERO;
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TipoIngressoInvalidoException("O preço de um tipo de ingresso pago deve ser maior que zero.");
        }
        return preco;
    }

    private static BigDecimal taxaEfetiva(boolean gratuito, BigDecimal taxaServico) {
        if (gratuito) {
            return ZERO;
        }
        if (taxaServico == null || taxaServico.compareTo(BigDecimal.ZERO) < 0) {
            throw new TipoIngressoInvalidoException("A taxa de serviço não pode ser negativa.");
        }
        return taxaServico;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new TipoIngressoInvalidoException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public Long sessaoId() {
        return sessaoId;
    }

    public String nome() {
        return nome;
    }

    public boolean gratuito() {
        return gratuito;
    }

    public BigDecimal preco() {
        return preco;
    }

    public BigDecimal taxaServico() {
        return taxaServico;
    }

    public int quantidadeTotal() {
        return quantidadeTotal;
    }

    public int quantidadeVendida() {
        return quantidadeVendida;
    }

    public int limitePorCompra() {
        return limitePorCompra;
    }

    public LocalDate inicioVendas() {
        return inicioVendas;
    }

    public LocalDate fimVendas() {
        return fimVendas;
    }

    /** Valor de bloqueio otimista lido do banco; repassado ao adaptador de persistência em {@code salvar()}. */
    public long versao() {
        return versao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoIngresso outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
