package com.chezticket.vendas.pedido.adapter.out.identidade;

import com.chezticket.identidade.conta.application.port.in.ConsultarUsuarioUseCase;
import com.chezticket.vendas.pedido.application.port.out.ConsultarEmailDoParticipantePort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de saída que liga o módulo de pedido ao módulo de conta, reusando a porta pública
 * {@link ConsultarUsuarioUseCase} — único ponto de acoplamento entre os dois módulos.
 */
@Component
class ConsultarEmailDoParticipanteAdapter implements ConsultarEmailDoParticipantePort {

    private final ConsultarUsuarioUseCase consultarUsuario;

    ConsultarEmailDoParticipanteAdapter(ConsultarUsuarioUseCase consultarUsuario) {
        this.consultarUsuario = consultarUsuario;
    }

    @Override
    public String buscarEmail(Long participanteId) {
        return consultarUsuario.buscarPorId(participanteId).email();
    }
}
