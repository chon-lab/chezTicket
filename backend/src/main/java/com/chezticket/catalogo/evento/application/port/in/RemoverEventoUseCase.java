package com.chezticket.catalogo.evento.application.port.in;

public interface RemoverEventoUseCase {

    /**
     * Remove um evento.
     *
     * @param eventoId      evento a remover
     * @param solicitanteId usuário que está pedindo a remoção (pode ser {@code null});
     *                      só é checado quando o evento já saiu de RASCUNHO
     */
    void remover(Long eventoId, Long solicitanteId);
}
