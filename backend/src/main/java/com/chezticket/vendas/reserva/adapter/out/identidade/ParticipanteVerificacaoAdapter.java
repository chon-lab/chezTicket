package com.chezticket.vendas.reserva.adapter.out.identidade;

import com.chezticket.vendas.reserva.application.port.out.VerificarParticipantePort;
import org.springframework.stereotype.Component;

@Component
class ParticipanteVerificacaoAdapter implements VerificarParticipantePort {

    private final ParticipanteExistenciaJpaRepository participantes;

    ParticipanteVerificacaoAdapter(ParticipanteExistenciaJpaRepository participantes) {
        this.participantes = participantes;
    }

    @Override
    public boolean participanteExiste(Long participanteId) {
        return participanteId != null && participantes.existsById(participanteId);
    }
}
