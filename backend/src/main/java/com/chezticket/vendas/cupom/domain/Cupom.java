package com.chezticket.vendas.cupom.domain;

import com.chezticket.vendas.cupom.domain.exception.CupomInvalidoException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Cupom de desconto percentual associado a um único evento. Modelo de domínio puro.
 *
 * <p>Modelo simplificado: o esquema original (Figura 3) modela cupom como entidade reutilizável
 * com limite de usos, resgatada em um {@code Pedido}. Como o front cria um cupom já junto com o
 * evento, e não existe {@code Pedido} (Fase 5, Stripe, ainda não implementada), este domínio
 * cobre só o que é usado de fato: um código por evento, com desconto percentual e validade.
 */
public class Cupom {

    private static final Pattern CODIGO = Pattern.compile("^[A-Z0-9]{8}$");

    private Long id;
    private final Long eventoId;
    private final String codigo;
    private final int percentualDesconto;
    private final LocalDate validoAte;

    private Cupom(Long id, Long eventoId, String codigo, int percentualDesconto, LocalDate validoAte) {
        this.id = id;
        this.eventoId = eventoId;
        this.codigo = codigo;
        this.percentualDesconto = percentualDesconto;
        this.validoAte = validoAte;
    }

    /** Cria um cupom novo para um evento. */
    public static Cupom criar(Long eventoId, String codigo, int percentualDesconto, LocalDate validoAte) {
        exigir(eventoId != null, "O evento do cupom é obrigatório.");
        exigir(percentualDesconto >= 1 && percentualDesconto <= 100,
                "O desconto deve ser entre 1% e 100%.");
        return new Cupom(null, eventoId, validarCodigo(codigo), percentualDesconto, validoAte);
    }

    /** Reconstrói um cupom já existente. Usado pelo adaptador de persistência. */
    public static Cupom reconstituir(Long id, Long eventoId, String codigo, int percentualDesconto,
            LocalDate validoAte) {
        return new Cupom(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(eventoId, "eventoId"),
                codigo,
                percentualDesconto,
                validoAte);
    }

    /** Sem data de validade, o cupom nunca expira. */
    public boolean estaValido(LocalDate hoje) {
        return validoAte == null || !hoje.isAfter(validoAte);
    }

    private static String validarCodigo(String codigo) {
        String normalizado = codigo == null ? "" : codigo.strip().toUpperCase();
        if (!CODIGO.matcher(normalizado).matches()) {
            throw new CupomInvalidoException("O código do cupom deve ter exatamente 8 caracteres alfanuméricos.");
        }
        return normalizado;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new CupomInvalidoException(mensagem);
        }
    }

    public Long id() {
        return id;
    }

    public Long eventoId() {
        return eventoId;
    }

    public String codigo() {
        return codigo;
    }

    public int percentualDesconto() {
        return percentualDesconto;
    }

    public LocalDate validoAte() {
        return validoAte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cupom outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
