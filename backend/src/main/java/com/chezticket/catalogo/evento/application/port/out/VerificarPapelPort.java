package com.chezticket.catalogo.evento.application.port.out;

/**
 * Porta de saída: o módulo de eventos precisa saber se quem está chamando é
 * administrador. Hoje é resolvida a partir de um identificador de usuário informado
 * pelo próprio chamador (não há autenticação ainda); quando a autenticação existir,
 * só esta implementação muda — a porta e o serviço continuam os mesmos.
 */
public interface VerificarPapelPort {

    boolean ehAdministrador(Long usuarioId);
}
