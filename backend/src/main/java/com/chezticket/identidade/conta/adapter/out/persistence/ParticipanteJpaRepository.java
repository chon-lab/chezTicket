package com.chezticket.identidade.conta.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface ParticipanteJpaRepository extends JpaRepository<ParticipanteJpaEntity, Long> {

    boolean existsByCpf(String cpf);
}
