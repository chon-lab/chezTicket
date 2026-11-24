package com.chezticket.identidade.perfil.application;

import com.chezticket.identidade.conta.domain.Usuario;
import java.util.List;

public record PerfilAgregado(
        Usuario usuario,
        List<EventoOrganizadoPerfil> eventos,
        List<ItemDeReservaPerfil> itensDeReserva) {
}
