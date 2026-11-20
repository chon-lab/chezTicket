package com.chezticket.catalogo.evento.application.port.in;

public interface RemoverEventoUseCase {

    /**
     * Remove um evento.
     *
     * @param eventoId      evento a remover
     * @param solicitanteId usuário que está pedindo a remoção; somente o organizador do
     *                      evento ou um administrador pode removê-lo
     */
    void remover(Long eventoId, Long solicitanteId);
}
